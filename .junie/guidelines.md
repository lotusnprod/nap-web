# NapraBase Development Guidelines

This document provides essential information for developers working on the NapraBase project.

## Build/Configuration Instructions

### Prerequisites

- JDK 23 (Eclipse Temurin recommended)
- Gradle (wrapper included)
- Docker or Podman with Compose

### Local Development Setup

1. **Clone the repository**

2. **Environment Variables**:
   - `SPARQL_SERVER`: URL of the SPARQL endpoint (default: http://nap-sparql:3030/raw/sparql)
   - `ADMIN_PASSWORD`: Password for the Fuseki admin interface
   - Optional authentication variables:
     - `HTTP_AUTH_SPARQL_USER`: Username for SPARQL endpoint authentication
     - `HTTP_AUTH_SPARQL_PASSWORD`: Password for SPARQL endpoint authentication

3. **Build the application**:
   ```bash
   ./gradlew build
   ```

4. **Run locally with Docker/Podman**:
   ```bash
   ./run
   ```
   This script:
   - Creates a Docker network called `web_network` if it doesn't exist
   - Stops any running containers
   - Removes any existing nap-web containers
   - Builds and starts the application using docker-compose

5. **Access the application**:
   - Web interface: http://localhost:8080
   - SPARQL endpoint: http://localhost:3030 (in development mode)

### Manual Build and Run

If you prefer not to use Docker/Podman:

1. **Build the application**:
   ```bash
   ./gradlew build
   ```

2. **Run the application**:
   ```bash
   ./gradlew run
   ```
   Note: You'll need to set up a SPARQL endpoint separately and configure the `SPARQL_SERVER` environment variable.

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

- **Kotlin**: Programming language (version 2.1.10)
- **Ktor**: Web framework (version 3.1.1)
- **Apache Jena**: RDF/semantic web library (version 5.3.0)
- **KotlinX HTML**: HTML DSL for Kotlin
- **Gradle**: Build system
- **Docker/Podman**: Containerization

### Architecture

The application follows a typical Ktor architecture:
- **Application.kt**: Configures and starts the Ktor application
- **Routing.kt**: Defines the HTTP routes
- **Pages**: Handle rendering of HTML pages
- **RDF**: Interacts with the SPARQL endpoint to retrieve data

### Docker Containers

The application consists of two main containers:
1. **nap-web**: The Ktor web application
2. **nap-sparql**: Apache Fuseki SPARQL endpoint

These containers are connected via the `web_network` Docker network.

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
- In development mode, the SPARQL endpoint is accessible at http://localhost:3030
- Check logs for errors and debugging information
