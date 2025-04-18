# NapraBase Web Application Guidelines

## Build & Run Commands
- Build project: `./gradlew build`
- Run development server: `./run` (uses docker-compose-dev.yml)
- Deploy to production: `scripts/deploy`
- Run with Docker: `docker-compose up`

## Environment Configuration
- Key variables: SPARQL_SERVER, HTTP_AUTH_SPARQL_USER, HTTP_AUTH_SPARQL_PASSWORD, ADMIN_PASSWORD
- Set development mode in application.conf

## Code Style Guidelines
- Follow official Kotlin style: kotlin.code.style=official
- 4-space indentation
- Camel case for functions and variables, Pascal case for classes
- Organize imports alphabetically
- Group imports by package
- Use kotlinx.html for HTML templating
- Prefer val over var
- Use nullable types only when necessary
- Handle errors with try/catch blocks or Result type
- Document public APIs with KDoc comments

## Project Structure
- Main application code in src/main/kotlin/net/nprod/nap/
- RDF/SPARQL related code in rdf/ directory
- Page templates in pages/ directory
- Data types in types/ directory