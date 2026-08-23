#!/bin/bash
# Idempotent local-dev seeder. Loads every .n3/.ttl/.nt under /seed into the TDB2
# store described by $ASSEMBLER, then builds the Lucene text index.
# No-op if the store already contains data (unless FORCE_SEED=true), so it is safe
# to run on every `make dev` and safe to point at a real store.
set -euo pipefail

STORE=/databases/tdb_nap_raw
LUCENE=/databases/nap_lucene

# Hard off-switch, so a stack pointed at a real store can never be seeded with
# fixtures even if the store somehow looks empty. Set by docker-compose.prod.yml
# and docker-compose.localdata.yml.
if [ "${SEED_ENABLED:-true}" != "true" ]; then
  echo "[seed] SEED_ENABLED=${SEED_ENABLED:-true}; refusing to touch $STORE."
  exit 0
fi

if [ -d "$STORE" ] && [ -n "$(ls -A "$STORE" 2>/dev/null)" ] && [ "${FORCE_SEED:-false}" != "true" ]; then
  echo "[seed] $STORE already populated; skipping. Set FORCE_SEED=true to reload."
  exit 0
fi

shopt -s nullglob
files=(/seed/*.n3 /seed/*.ttl /seed/*.nt)
if [ ${#files[@]} -eq 0 ]; then
  echo "[seed] no seed files in /seed — nothing to do."
  exit 0
fi

echo "[seed] wiping $STORE and $LUCENE"
rm -rf "$STORE" "$LUCENE"
mkdir -p "$STORE" "$LUCENE"

echo "[seed] loading ${#files[@]} file(s) into $STORE"
java -cp "$FUSEKI_HOME/fuseki-server.jar:/javalibs/*" tdb2.tdbloader \
     --desc="$ASSEMBLER" "${files[@]}"

echo "[seed] building Lucene text index"
java -cp "$FUSEKI_HOME/fuseki-server.jar:/javalibs/*" jena.textindexer \
     --desc="$ASSEMBLER"

echo "[seed] done."
