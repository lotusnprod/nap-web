package net.nprod.nap.pages.pharmacologySearch

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import net.nprod.nap.rdf.SparqlConnector
import net.nprod.nap.rdf.pharmacologySearchQuery
import org.apache.jena.query.QuerySolution

/**
 * Controller for the pharmacology search page
 * Handles requests to search for pharmacology entries
 */
class PharmacologySearchController {
    private val sparqlConnector = SparqlConnector()
    
    /**
     * Handle GET request to /pharmacology/search
     * Searches for pharmacology entries matching the query parameter
     *
     * @param call The application call
     */
    suspend fun handleRequest(call: ApplicationCall) {
        val queryParam = call.request.queryParameters["query"]
        val pharmacologyEntries = if (!queryParam.isNullOrBlank()) {
            val results = sparqlConnector.getResultsOfQuery(pharmacologySearchQuery(queryParam))

            val entries = mutableListOf<Map<String, String>>()
            
            while (results != null && results.hasNext()) {
                val solution: QuerySolution = results.nextSolution()
                val pharmacologyUri = solution.getResource("pharmacology").uri
                val name = solution.getLiteral("name").string

                entries.add(mapOf(
                    "uri" to pharmacologyUri,
                    "name" to name
                ))
            }
            
            entries
        } else {
            emptyList()
        }

        val data = PharmacologySearchViewData(queryParam, pharmacologyEntries)
        val html = PharmacologySearchView.render(data)
        call.respondText(html, ContentType.Text.Html)
    }

    companion object {
        /**
         * Register routes for the pharmacology search controller
         *
         * @param routing The routing configuration to add routes to
         */
        fun registerRoutes(routing: Routing) {
            val controller = PharmacologySearchController()
            routing.get("/pharmacology/search") {
                controller.handleRequest(call)
            }
        }
    }
}