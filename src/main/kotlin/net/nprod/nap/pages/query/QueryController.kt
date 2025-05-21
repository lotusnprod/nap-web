package net.nprod.nap.pages.query

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import net.nprod.nap.rdf.SparqlConnector

/**
 * Controller for the SPARQL query page
 * Handles requests to the /sparql route
 */
class QueryController {
    /**
     * Handle GET request to /sparql
     * Renders the query page with the SPARQL editor
     *
     * @param call The application call
     */
    suspend fun handleRequest(call: ApplicationCall) {
        val data = QueryViewData()
        val html = QueryView.render(data)
        call.respondText(html, ContentType.Text.Html)
    }

    companion object {
        /**
         * Register routes for the query controller
         *
         * @param routing The routing configuration to add routes to
         */
        fun registerRoutes(routing: Routing) {
            val controller = QueryController()
            routing.get("/sparql") {
                controller.handleRequest(call)
            }
        }
    }
}