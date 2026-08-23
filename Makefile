# Single entry point for local development. Replaces the old run/run-dev/run-text-indexer
# scripts. Engine detection: prefer docker, fall back to podman-compose.
COMPOSE := $(shell \
  if docker compose version >/dev/null 2>&1; then echo "docker compose"; \
  elif command -v podman-compose >/dev/null 2>&1; then echo "podman-compose"; \
  else echo "MISSING"; fi)

# docker-compose.override.yml is auto-loaded only when no -f is passed, so the
# localdata flavour has to list every file explicitly.
LOCALDATA_FILES := -f docker-compose.yml -f docker-compose.override.yml -f docker-compose.localdata.yml

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
	$(COMPOSE) up --build -d --wait
	@echo ""
	@echo "  nap-web   http://localhost:$${WEB_PORT:-8080}"
	@echo "  fuseki    http://localhost:$${FUSEKI_PORT:-3030}"
	@echo ""

dev-localdata: check-engine env ## Like dev, but against the store at NAP_DATA_DIR; never seeds
	$(COMPOSE) $(LOCALDATA_FILES) up --build -d --wait

up: check-engine env ## Start without rebuilding
	$(COMPOSE) up -d --wait

sparql-only: check-engine env ## Only Fuseki (for running nap-web from the IDE)
	$(COMPOSE) up --build -d --wait nap-sparql
	@echo "Run the app with:"
	@echo "  SPARQL_SERVER=$(SPARQL_ENDPOINT) ENVIRONMENT=development ./gradlew run"

down: check-engine ## Stop the stack (keeps data)
	$(COMPOSE) down

logs: check-engine ## Follow logs
	$(COMPOSE) logs -f

ps: check-engine ## Show container + health status
	$(COMPOSE) ps

reseed: check-engine ## Wipe the TDB2 store and reload stack/seed/*
	FORCE_SEED=true $(COMPOSE) up -d --force-recreate nap-sparql

reindex: check-engine ## Rebuild the Lucene text index (stack must be down)
	$(COMPOSE) stop nap-sparql
	$(COMPOSE) run --rm --entrypoint sh nap-sparql -c \
	  'java -cp "$$FUSEKI_HOME/fuseki-server.jar:/javalibs/*" jena.textindexer --desc=$$ASSEMBLER'
	$(COMPOSE) start nap-sparql

shell-web: check-engine ## Shell into nap-web
	$(COMPOSE) exec nap-web sh

shell-sparql: check-engine ## Shell into nap-sparql
	$(COMPOSE) exec nap-sparql sh

test: ## Run the test suite with a coverage report
	./gradlew build koverXmlReport

build: ## Build the distribution locally
	./gradlew installDist

image: check-engine ## Build container images only
	$(COMPOSE) build

clean: check-engine ## Stop and remove containers (keeps the data volume)
	$(COMPOSE) down --remove-orphans

nuke: check-engine ## Stop and DELETE the data volume
	$(COMPOSE) down -v --remove-orphans
