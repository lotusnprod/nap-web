package net.nprod.nap.pages.compoundSearch

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import net.nprod.nap.rdf.SparqlConnector
import net.nprod.nap.rdf.compoundSearchQuery
import org.apache.jena.query.QuerySolution

/**
 * Controller for the compound search page
 * Handles requests to search for compounds
 */
class CompoundSearchController {
    private val sparqlConnector = SparqlConnector()
    
    /**
     * Handle GET request to /compound/search
     * Searches for compounds matching the query parameter
     *
     * @param call The application call
     */
    suspend fun handleRequest(call: ApplicationCall) {
        // Start tracking SPARQL queries for this request
        SparqlConnector.startTracking()
        
        try {
            val queryParam = call.request.queryParameters["query"]
            val compounds = if (!queryParam.isNullOrBlank()) {
                val results = sparqlConnector.getResultsOfQuery(compoundSearchQuery(queryParam))
                val compoundList = mutableListOf<Map<String, String>>()
                
                while (results != null && results.hasNext()) {
                    val solution: QuerySolution = results.nextSolution()
                    val compoundUri = solution.getResource("compound").uri
                    val name = solution.getLiteral("name").string
                    val compoundClass = solution.getLiteral("compoundClass")?.string ?: "Unknown"
                    val number = solution.getLiteral("number")?.int?.toString() ?: "Unknown"

                    compoundList.add(mapOf(
                        "uri" to compoundUri,
                        "name" to name,
                        "compoundClass" to compoundClass,
                        "number" to number
                    ))
                }
                
                compoundList
            } else {
                emptyList()
            }

            val data = CompoundSearchViewData(queryParam, compounds)
            val html = CompoundSearchView.render(data)
            call.respondText(html, ContentType.Text.Html)
        } finally {
            // Clear tracking after request is complete
            SparqlConnector.clearTracking()
        }
    }

    companion object {
        /**
         * Register routes for the compound search controller
         *
         * @param routing The routing configuration to add routes to
         */
        fun registerRoutes(routing: Routing) {
            val controller = CompoundSearchController()
            routing.get("/compound/search") {
                controller.handleRequest(call)
            }
        }
    }
}