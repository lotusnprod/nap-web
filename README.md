# nap-web

Web front-end for Nap — **Natural Actives & Pharmacology** — an open database of
ethnomedical and ethnopharmacological information derived from a subset of NAPRALERT.
Kotlin + Ktor, served from an Apache Jena Fuseki SPARQL endpoint.

Code is licensed under AGPL 3.0 except for SnorQL by Ammar Ammar (ammar257ammar@gmail.com)
and collaborators, which is GPL v3.0.

## Quick start

Requires JDK 26 and either Docker (with Compose v2) or Podman with `podman-compose`.

```shell
make dev
```

That builds both images, loads the fixtures in `stack/seed/` into a fresh TDB2 store,
builds the Lucene index, and waits until every service is healthy. Then:

- app — <http://localhost:8080>
- an example page — <http://localhost:8080/compound/1>
- Fuseki UI — <http://localhost:3030>

`make help` lists every target. The useful ones:

| Target | What it does |
|---|---|
| `make dev` | Build + start the whole seeded stack, wait for health |
| `make up` | Start without rebuilding |
| `make sparql-only` | Only Fuseki, for running the app from the IDE |
| `make reseed` | Wipe the store and reload `stack/seed/*` |
| `make reindex` | Rebuild the Lucene text index |
| `make logs` / `make ps` | Follow logs / show health status |
| `make down` | Stop, keeping the data volume |
| `make nuke` | Stop and delete the data volume |
| `make test` | `./gradlew build koverXmlReport` |

## Configuration

`make dev` copies `.env.example` to `.env` on first run; `.env` is gitignored. Compose
reads it for `ADMIN_PASSWORD`, `WEB_PORT`, `ENVIRONMENT`, `QUERY_TIMEOUT`, the optional
`HTTP_AUTH_SPARQL_*` credentials and the resource limits.

`ADMIN_PASSWORD` has no default — compose fails loudly if it is unset. It guards only
Fuseki's `/$/**` admin API; data access never consults it.

## Seed data

`stack/seed/*.n3` is the single source of truth for RDF fixtures. The dev stack loads every
file there through `stack/fuseki/seed-entrypoint.sh`, and `build.gradle.kts` puts the same
directory on the test classpath, so the tests read the same triples rather than a copy.

The in-memory test server loads `001-core.n3` (the default in `InMemoryFusekiServer.start()`);
pass a resource name to load a different one. So a new `002-*.n3` reaches `make dev`
immediately and any test that asks for it by name.

Seeding happens in the Fuseki container's entrypoint, before Fuseki opens the store, and is
idempotent: it is skipped when the store already has data. `make reseed` (i.e.
`FORCE_SEED=true`) wipes and reloads.

## Running against a real (non-fixture) store

To point the stack at an existing TDB2 store — a copy of production, for instance —
instead of the seeded volume:

```shell
NAP_DATA_DIR=$PWD/data make dev-localdata
```

Seeding is disabled outright in that mode (`SEED_ENABLED=false`), so the store cannot be
overwritten. On rootless Podman the directory has to be owned by the container user once:

```shell
podman unshare chown -R 9008:0 "$NAP_DATA_DIR"
```

If the `nap-data` volume already exists with different backing, `docker volume rm nap_nap-data` first.

## Running the app from the IDE

```shell
make sparql-only
SPARQL_SERVER=http://localhost:3030/raw/sparql ENVIRONMENT=development ./gradlew run
```

## Health endpoints

- `/health` — liveness; the container `HEALTHCHECK` and compose probe this.
- `/health/ready` — readiness; returns 503 unless the SPARQL endpoint answers with data.
  A green `/health` and a red `/health/ready` means Fuseki is the problem, not the app.

## Production

The server uses the explicit production overlay — `docker-compose.override.yml` is
deliberately not loaded:

```shell
NAP_DATA_DIR=/srv/nap/data docker compose -f docker-compose.yml -f docker-compose.prod.yml up -d --wait
```

It bind-mounts the real store, disables the seeder, joins nap-web to the external
`web_network` reverse-proxy network, and applies the memory/CPU limits from `.env`.
