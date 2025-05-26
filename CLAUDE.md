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

## Data Conventions
- Use integers for entity numbers (compound numbers, organism numbers, etc.)
- When creating test data or RDF triples, always use integer literals for `n:number` fields, not strings
- Entity URIs should use numeric identifiers (e.g., `/compound/1`, `/organism/123`)

## Project Structure
- Main application code in src/main/kotlin/net/nprod/nap/
- RDF/SPARQL related code in rdf/ directory
- Page templates in pages/ directory
- Data types in types/ directory

## Architecture Overview
The application follows an MVC-like architecture pattern:

### Controller Layer
- **AbstractController**: Abstract base class that defines the core structure for all entity controllers
  - Provides common methods for data retrieval and rendering
  - Handles error cases consistently
  - Creates data objects from SPARQL query results
  - Connects views with data
  
- **BaseController**: Interface that defines the contract for controllers
  - Handles HTTP request processing with content negotiation (HTML/JSON)
  - Manages routing and parameter extraction
  - Standardizes error responses
  - Provides route registration utilities
  
- **Entity Controllers**: Entity-specific controllers (e.g., AdministrationRouteController, CompoundController)
  - Implement entity-specific data retrieval logic
  - Define entity type information
  - Register routes for the entity
  - Connect to the appropriate view renderer

### View Layer
- **View Objects**: Entity-specific view renderers (e.g., AdministrationRouteView, CompoundView)
  - Responsible for HTML rendering using kotlinx.html
  - Provide clean separation between data and presentation
  - Use shared templates and components
  - Define the structure and layout of pages

### Model/Data Layer
- **ViewData Classes**: Data transfer objects (e.g., AdministrationRouteViewData, CompoundViewData)
  - Contain all necessary data for view rendering
  - Serializable for both HTML rendering and JSON API responses
  - Act as an interface between controllers and views
  - Bundle related data entities together

### Routing
- Routes are registered in the Routing.kt file
- Entity controllers register their own routes using the registerRoutes function
- Legacy handlers still exist for some routes

## Creating a New Entity Page
1. Create a new directory under pages/ with your entity name
2. Create these files in the directory:
   - `EntityViewData.kt`: Define the data structure
   - `EntityView.kt`: Create the view rendering logic
   - `EntityController.kt`: Implement data retrieval and routing
3. Register the controller routes in Routing.kt

## Example Flow
1. Request comes to `/compound/123`
2. CompoundController processes the request
3. Controller retrieves data from SPARQL endpoint
4. Data is packaged into CompoundViewData object
5. CompoundView renders HTML using the data
6. Response is sent to the client

## Tools

### Coverage Parser
Location: `tools/coverage_parser.py`

A utility to parse Kover coverage reports and identify classes that need more test coverage.

Usage:
```bash
# Show classes with coverage < 100% (default)
./tools/coverage_parser.py

# Show specific coverage ranges
./tools/coverage_parser.py --min 50 --max 90

# Filter by file name
./tools/coverage_parser.py --file Citation

# Show more results
./tools/coverage_parser.py --limit 50

# Sort by different criteria
./tools/coverage_parser.py --sort name
```

Note: When improving test coverage, ignore null checks on `getResultsOfQuery` in SparqlConnector as they require mocking which isn't necessary.