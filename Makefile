# Single entry point for local development. Replaces the old run/run-dev/run-text-indexer
# scripts. Engine detection: prefer docker, fall back to podman-compose.
COMPOSE := $(shell \
  if docker compose version >/dev/null 2>&1; then echo "docker compose"; \
  elif command -v podman-compose >/dev/null 2>&1; then echo "podman-compose"; \
  else echo "MISSING"; fi)

# docker-compose.override.yml is auto-loaded only when no -f is passed, so the
# localdata flavour has to list every file explicitly.
LOCALDATA_FILES := -f docker-compose.yml -f docker-compose.override.yml -f docker-compose.localdata.yml

# compose reads .env by itself, make does not. Read it here too so the preflight
# checks the same value compose will use, and export it so both agree. This has to
# come before the file selection below, which branches on it.
NAP_DATA_DIR ?= $(shell sed -n 's/^NAP_DATA_DIR=//p' .env 2>/dev/null | tail -1)
export NAP_DATA_DIR

# Compose file selection. Dev on the seeded fixture volume is the default (the
# override file is auto-loaded).
#
# `PROD=1` selects the production overlay — REQUIRED on the server, where the real
# store is a bind mount that only docker-compose.prod.yml declares, and where the
# dev override would publish Fuseki on the host and flip nap-web to development
# mode. Without it every target below silently runs the dev config.
#
# Otherwise, setting NAP_DATA_DIR runs the dev stack against that store instead of
# the fixtures, seeding disabled. It is the variable the localdata overlay reads, so
# having it select the overlay too is the only way `NAP_DATA_DIR=... make sparql-only`
# can mean what it looks like it means; before, it was accepted and ignored, and you
# got the fixtures with nothing in the output to say so.
ifeq ($(PROD),1)
COMPOSE_FILES := -f docker-compose.yml -f docker-compose.prod.yml
PREFLIGHT := store-preflight
else ifneq ($(strip $(NAP_DATA_DIR)),)
COMPOSE_FILES := $(LOCALDATA_FILES)
PREFLIGHT := store-preflight
else
COMPOSE_FILES :=
PREFLIGHT :=
endif
DC := $(COMPOSE) $(COMPOSE_FILES)

SPARQL_ENDPOINT ?= http://localhost:3030/raw/sparql

.DEFAULT_GOAL := help
.PHONY: help dev dev-localdata up sparql-only wait-sparql down logs ps reseed reindex \
        shell-web shell-sparql test build image clean nuke check-engine env store-preflight

help: ## Show this help
	@grep -hE '^[a-zA-Z_-]+:.*?## ' $(MAKEFILE_LIST) | \
	  awk 'BEGIN{FS=":.*?## "}{printf "  \033[36m%-16s\033[0m %s\n", $$1, $$2}'

check-engine:
	@test "$(COMPOSE)" != "MISSING" || { echo "Need docker compose or podman-compose"; exit 1; }

# Docker creates a missing bind source instead of failing, so a wrong NAP_DATA_DIR
# would bring the stack up on an empty store and look healthy. Check it here, before
# compose gets a chance. Runs for both flavours that bind a real store: PROD=1 and
# NAP_DATA_DIR on its own.
store-preflight:
	@test -n "$(NAP_DATA_DIR)" || { \
	  echo "NAP_DATA_DIR is not set (put it in .env, e.g. NAP_DATA_DIR=/home/bjo/nap/nap-web/data)"; exit 1; }
	@test -d "$(NAP_DATA_DIR)" || { \
	  echo "NAP_DATA_DIR=$(NAP_DATA_DIR) does not exist. Refusing: docker would create it empty."; exit 1; }
	@test -d "$(NAP_DATA_DIR)/tdb_nap_raw" || { \
	  echo "NAP_DATA_DIR=$(NAP_DATA_DIR) has no tdb_nap_raw/ - is that really the store?"; exit 1; }
	@echo "[preflight] store: $(NAP_DATA_DIR) ($$(du -sh $(NAP_DATA_DIR)/tdb_nap_raw 2>/dev/null | cut -f1))"

env:
	@test -f .env || { cp .env.example .env; echo "Created .env from .env.example"; }

dev: check-engine $(PREFLIGHT) env ## Full local stack with seed data (the one command you need)
	$(DC) up --build -d --wait
	@echo ""
	@echo "  nap-web   http://localhost:$${WEB_PORT:-8080}"
	@echo "  fuseki    http://localhost:$${FUSEKI_PORT:-3030}"
	@echo ""

# Kept as a name people already type. Setting NAP_DATA_DIR is now enough on its own,
# so this is `make dev` with the variable required rather than merely honoured.
dev-localdata: check-engine store-preflight env ## Like dev, but against the store at NAP_DATA_DIR; never seeds
	$(COMPOSE) $(LOCALDATA_FILES) up --build -d --wait

up: check-engine $(PREFLIGHT) env ## Start without rebuilding (PROD=1 on the server)
	$(DC) up -d --wait

# Two things `up --build -d --wait nap-sparql` got wrong once a container existed:
#   - compose reuses the container it finds, rebuilt image or not, so the second run
#     silently left the old Fuseki running;
#   - podman-compose's `--wait` waits for nothing. It prints "Error: container is
#     stopped" and returns, which reads as a failure and tells you nothing about
#     whether the server came up.
# So: tear the project down, bring Fuseki back alone, and ask it ourselves. Taking
# nap-web down with it is the point of the target — it is the process you are about
# to run from the IDE, on the same port.
sparql-only: check-engine $(PREFLIGHT) env ## Only Fuseki, recreated from scratch (for running nap-web from the IDE)
	@test "$(PROD)" != "1" || { echo "sparql-only is a dev target; refusing to run with PROD=1"; exit 1; }
	@# podman-compose reports every service of the project that has no container as an
	@# error, so a clean tree brings back five "no such container" lines that are not
	@# failures. Anything that actually went wrong shows up on the `up` below.
	@$(DC) down 2>&1 | grep -vE 'no such (container|pod)' || true
	$(DC) up --build -d nap-sparql
	@$(MAKE) --no-print-directory wait-sparql
	@echo "Run the app with:"
	@echo "  SPARQL_SERVER=$(SPARQL_ENDPOINT) ENVIRONMENT=development ./gradlew run"

wait-sparql: ## Block until Fuseki answers a query
	@command -v curl >/dev/null 2>&1 || { echo "[wait] no curl, skipping the readiness check"; exit 0; }
	@printf '[wait] fuseki '
	@for i in $$(seq 1 90); do \
	  if curl -sf -o /dev/null --max-time 2 "$(SPARQL_ENDPOINT)?query=ASK%7B%7D"; then echo "ready"; exit 0; fi; \
	  printf '.'; sleep 2; \
	done; \
	echo "not ready after 180s"; $(DC) logs --tail 30 nap-sparql || true; exit 1

down: check-engine ## Stop the stack (keeps data)
	$(DC) down

logs: check-engine ## Follow logs
	$(DC) logs -f

ps: check-engine ## Show container + health status
	$(DC) ps

reseed: check-engine $(PREFLIGHT) ## Wipe the TDB2 store and reload stack/seed/* (fixture volume only)
	@test "$(PROD)" != "1" || { echo "reseed loads FIXTURES; refusing to run with PROD=1"; exit 1; }
	@# The entrypoint would refuse anyway (the localdata overlay pins SEED_ENABLED=false),
	@# but silently: the target would report success having done nothing.
	@test -z "$(strip $(NAP_DATA_DIR))" || { \
	  echo "reseed loads FIXTURES; refusing while NAP_DATA_DIR=$(NAP_DATA_DIR) selects a real store"; exit 1; }
	FORCE_SEED=true $(DC) up -d --force-recreate nap-sparql

# Two things this target has to get right, both of which bit us once:
#   - the compose file set (PROD=1), or it indexes whatever store the dev config
#     points at rather than the real one;
#   - logging. The image ships log4j2.properties but it is not on the classpath of
#     a bare `java -cp fuseki-server.jar`, so log4j2 falls back to its ERROR-only
#     default and the indexer's "N properties indexed" summary is invisible — the
#     command prints nothing whether it indexed six million properties or zero.
reindex: check-engine $(PREFLIGHT) ## Rebuild the Lucene text index (add PROD=1 on the server)
	$(DC) stop nap-sparql
	$(DC) run --rm --entrypoint sh nap-sparql -c '\
	  set -e; \
	  test -n "$$(ls -A /databases/tdb_nap_raw 2>/dev/null)" || { \
	    echo "ERROR: /databases/tdb_nap_raw is empty."; \
	    echo "       On the server this means the wrong compose files: use PROD=1."; \
	    exit 1; }; \
	  echo "[reindex] store: $$(du -sh /databases/tdb_nap_raw | cut -f1)"; \
	  rm -rf /databases/nap_lucene.old; \
	  if [ -d /databases/nap_lucene ]; then mv /databases/nap_lucene /databases/nap_lucene.old; fi; \
	  mkdir -p /databases/nap_lucene; \
	  java -Dlog4j.configurationFile=$$FUSEKI_HOME/log4j2.properties \
	       -cp "$$FUSEKI_HOME/fuseki-server.jar:/javalibs/*" jena.textindexer --desc=$$ASSEMBLER; \
	  echo "[reindex] new index: $$(du -sh /databases/nap_lucene | cut -f1) (previous kept as nap_lucene.old)"'
	$(DC) start nap-sparql
	@echo "Check a search page, then delete nap_lucene.old from the data dir."

shell-web: check-engine ## Shell into nap-web
	$(DC) exec nap-web sh

shell-sparql: check-engine ## Shell into nap-sparql
	$(DC) exec nap-sparql sh

test: ## Run the test suite with a coverage report
	./gradlew build koverXmlReport

build: ## Build the distribution locally
	./gradlew installDist

image: check-engine ## Build container images only
	$(DC) build

clean: check-engine ## Stop and remove containers (keeps the data volume)
	$(DC) down --remove-orphans

nuke: check-engine ## Stop and DELETE the data volume (dev only)
	@test "$(PROD)" != "1" || { echo "nuke DELETES the data volume; refusing to run with PROD=1"; exit 1; }
	$(DC) down -v --remove-orphans
