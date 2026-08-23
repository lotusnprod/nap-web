# NapraBase Development Guidelines

This document provides essential information for developers working on the NapraBase project.

## Build/Configuration Instructions

### Prerequisites

- JDK 26 (Eclipse Temurin recommended)
- Gradle (wrapper included)
- Docker or Podman with Compose

### Local Development Setup

1. **Clone the repository**

2. **Start the stack**:
   ```bash
   make dev
   ```
   This builds both images, loads `stack/seed/*.n3` into a fresh TDB2 store, builds the
   Lucene index and waits until every service reports healthy. `make help` lists all
   targets. No network has to be created by hand and no data has to be provisioned.

3. **Access the application**:
   - Web interface: http://localhost:8080
   - Fuseki UI: http://localhost:3030

4. **Configuration**: `make dev` copies `.env.example` to `.env` on first run (gitignored).
   - `ADMIN_PASSWORD`: Fuseki admin API password. No default — compose fails if unset.
   - `SPARQL_SERVER`: SPARQL endpoint URL (compose sets http://nap-sparql:3030/raw/sparql)
   - `ENVIRONMENT`: `development` or `production`; controls link generation
   - `WEB_PORT`, `FUSEKI_PORT`: host ports
   - `QUERY_TIMEOUT`: Fuseki ARQ query timeout in ms
   - `HTTP_AUTH_SPARQL_USER` / `HTTP_AUTH_SPARQL_PASSWORD`: only needed when the SPARQL
     endpoint sits behind HTTP basic auth
   - `FORCE_SEED`: wipe the store and reload the seed files
   - `NAP_DATA_DIR`: absolute path to an existing TDB2 store, for `make dev-localdata`

### Running the app outside a container

```bash
make sparql-only
SPARQL_SERVER=http://localhost:3030/raw/sparql ENVIRONMENT=development ./gradlew run
```

`make sparql-only` starts just Fuseki, which is also the setup to use when running or
debugging the app from the IDE.

## Testing Information

### Running Tests

1. **Run all tests**:
   ```bash
   ./gradlew test
   ```

2. **Run a specific test class**:
   ```bash
   ./gradlew test --tests "net.nprod.nap.ApplicationTest"
   ```

3. **Run a specific test method**:
   ```bash
   ./gradlew test --tests "net.nprod.nap.ApplicationTest.testSimple"
   ```

### Adding New Tests

1. **Create test classes in the appropriate package under `src/test/kotlin`**:
   - Follow the same package structure as the main code
   - Name test classes with a `Test` suffix (e.g., `ApplicationTest`)

2. **Example test class**:
   ```kotlin
   class ExampleTest {
       @Test
       fun testFeature() {
           // Test code here
           assertTrue(true, "This test should pass")
       }
   }
   ```

3. **Testing Ktor Applications**:
   For testing Ktor applications, you would typically use the Ktor testing utilities:
   ```kotlin
   @Test
   fun testRoute() = testApplication {
       application {
           module()
       }

       client.get("/").apply {
           assertEquals(HttpStatusCode.OK, status)
           assertTrue(bodyAsText().contains("<html>"))
       }
   }
   ```
   Note: This requires the `io.ktor:ktor-server-tests-jvm` dependency with the correct version.

## Additional Development Information

### Project Structure

- **src/main/kotlin/net/nprod/nap**: Main application code
  - **Application.kt**: Main entry point
  - **helpers/**: Utility functions and classes
  - **pages/**: Web page definitions and routes
  - **plugins/**: Ktor plugins and configurations
  - **rdf/**: RDF/semantic web data processing
  - **types/**: Data classes and type definitions

### Key Technologies

Versions live in `gradle.properties` — that file is the source of truth, not this list.

- **Kotlin**: Programming language
- **Ktor**: Web framework
- **Apache Jena**: RDF/semantic web library
- **KotlinX HTML**: HTML DSL for Kotlin
- **Gradle**: Build system
- **Docker/Podman**: Containerization

### Architecture

The application follows a typical Ktor architecture:
- **Application.kt**: Configures and starts the Ktor application
- **Routing.kt**: Defines the HTTP routes
- **Pages**: Handle rendering of HTML pages
- **RDF**: Interacts with the SPARQL endpoint to retrieve data

### Containers

Two services, on an internal `nap` bridge network:

1. **nap-sparql**: Apache Fuseki SPARQL endpoint. Its entrypoint seeds `stack/seed/*.n3`
   into the TDB2 store and builds the Lucene index *before* starting Fuseki. That is
   idempotent — skipped when the store already has data, when `/seed` is empty, or when
   `SEED_ENABLED=false`. Seeding runs here rather than in a separate one-shot service
   because podman-compose turns `depends_on: service_completed_successfully` into a
   must-be-running requirement, which a container that exits can never satisfy.
2. **nap-web**: the Ktor web application. Starts only once Fuseki reports healthy.

Compose files:

- `docker-compose.yml` — engine-neutral base; named `nap-data` volume, healthchecks,
  `depends_on` conditions. No `:U` and no uidmaps, so Docker and Podman behave the same.
- `docker-compose.override.yml` — auto-loaded, so local dev is the default: publishes the
  Fuseki port and forces `ENVIRONMENT=development`.
- `docker-compose.localdata.yml` — point the stack at an existing TDB2 store instead of the
  seeded volume (`make dev-localdata`, needs `NAP_DATA_DIR`). Seeding is disabled.
- `docker-compose.prod.yml` — server-side: bind-mounted real store, seeding disabled,
  nap-web also joined to the external `web_network` reverse-proxy network, resource limits.

### Health endpoints

- `/health` — liveness; used by the container `HEALTHCHECK` and the compose healthcheck.
- `/health/ready` — readiness; 503 unless the SPARQL endpoint answers with data.

### Code Style

- Follow Kotlin coding conventions
- Use meaningful variable and function names
- Add comments for complex logic
- Write tests for new functionality

### Debugging

- Set the `development` property in Gradle to enable development mode:
  ```bash
  ./gradlew run -Pdevelopment=true
  ```
- `make sparql-only` gives you just Fuseki on http://localhost:3030 to run the app against
- `make logs` / `make ps` for container output and health status
- `make shell-sparql` for a shell in the Fuseki container (the `TDB*`/`TEXTINDEXER` env vars
  there are ready-made loader commands)
