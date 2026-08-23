# Single entry point for local development. Replaces the old run/run-dev/run-text-indexer
# scripts. Engine detection: prefer docker, fall back to podman-compose.
COMPOSE := $(shell \
  if docker compose version >/dev/null 2>&1; then echo "docker compose"; \
  elif command -v podman-compose >/dev/null 2>&1; then echo "podman-compose"; \
  else echo "MISSING"; fi)

# docker-compose.override.yml is auto-loaded only when no -f is passed, so the
# localdata flavour has to list every file explicitly.
LOCALDATA_FILES := -f docker-compose.yml -f docker-compose.override.yml -f docker-compose.localdata.yml

# Compose file selection. Dev is the default (the override file is auto-loaded).
# `PROD=1` selects the production overlay instead — REQUIRED on the server, where
# the real store is a bind mount that only docker-compose.prod.yml declares, and
# where the dev override would publish Fuseki on the host and flip nap-web to
# development mode. Without it every target below silently runs the dev config.
ifeq ($(PROD),1)
COMPOSE_FILES := -f docker-compose.yml -f docker-compose.prod.yml
else
COMPOSE_FILES :=
endif
DC := $(COMPOSE) $(COMPOSE_FILES)

SPARQL_ENDPOINT ?= http://localhost:3030/raw/sparql

.DEFAULT_GOAL := help
.PHONY: help dev dev-localdata up sparql-only down logs ps reseed reindex \
        shell-web shell-sparql test build image clean nuke check-engine env

help: ## Show this help
	@grep -hE '^[a-zA-Z_-]+:.*?## ' $(MAKEFILE_LIST) | \
	  awk 'BEGIN{FS=":.*?## "}{printf "  \033[36m%-16s\033[0m %s\n", $$1, $$2}'

check-engine:
	@test "$(COMPOSE)" != "MISSING" || { echo "Need docker compose or podman-compose"; exit 1; }

env:
	@test -f .env || { cp .env.example .env; echo "Created .env from .env.example"; }

dev: check-engine env ## Full local stack with seed data (the one command you need)
	$(DC) up --build -d --wait
	@echo ""
	@echo "  nap-web   http://localhost:$${WEB_PORT:-8080}"
	@echo "  fuseki    http://localhost:$${FUSEKI_PORT:-3030}"
	@echo ""

dev-localdata: check-engine env ## Like dev, but against the store at NAP_DATA_DIR; never seeds
	$(COMPOSE) $(LOCALDATA_FILES) up --build -d --wait

up: check-engine env ## Start without rebuilding (PROD=1 on the server)
	$(DC) up -d --wait

sparql-only: check-engine env ## Only Fuseki (for running nap-web from the IDE)
	$(DC) up --build -d --wait nap-sparql
	@echo "Run the app with:"
	@echo "  SPARQL_SERVER=$(SPARQL_ENDPOINT) ENVIRONMENT=development ./gradlew run"

down: check-engine ## Stop the stack (keeps data)
	$(DC) down

logs: check-engine ## Follow logs
	$(DC) logs -f

ps: check-engine ## Show container + health status
	$(DC) ps

reseed: check-engine ## Wipe the TDB2 store and reload stack/seed/* (dev only)
	@test "$(PROD)" != "1" || { echo "reseed loads FIXTURES; refusing to run with PROD=1"; exit 1; }
	FORCE_SEED=true $(DC) up -d --force-recreate nap-sparql

# Two things this target has to get right, both of which bit us once:
#   - the compose file set (PROD=1), or it indexes whatever store the dev config
#     points at rather than the real one;
#   - logging. The image ships log4j2.properties but it is not on the classpath of
#     a bare `java -cp fuseki-server.jar`, so log4j2 falls back to its ERROR-only
#     default and the indexer's "N properties indexed" summary is invisible — the
#     command prints nothing whether it indexed six million properties or zero.
reindex: check-engine ## Rebuild the Lucene text index (add PROD=1 on the server)
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
