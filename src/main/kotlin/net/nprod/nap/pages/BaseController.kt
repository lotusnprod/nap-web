package net.nprod.nap.pages

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.header
import io.ktor.server.response.*
import io.ktor.server.routing.*
import net.nprod.nap.rdf.SparqlConnector

/**
 * Base controller interface with support for HTML and JSON response formats
 * 
 * @param DataType The type of data this controller handles
 */
interface BaseController<DataType> {
    /**
     * Get data by identifier
     * 
     * @param identifier The entity identifier
     * @return The data or null if not found
     */
    fun getData(identifier: String?): DataType?
    
    /**
     * Render data as HTML
     * 
     * @param data The data to render
     * @return HTML string representation
     */
    fun renderHtml(data: DataType): String
    
    /**
     * Handle request with content negotiation
     * 
     * @param call The application call
     * @param id The entity identifier from the route parameters
     */
    suspend fun handleRequest(call: ApplicationCall, id: String?) {
        // Get the Accept header to determine the response format
        val acceptHeader = call.request.header(HttpHeaders.Accept)
        
        // Start tracking SPARQL queries for this request
        SparqlConnector.startTracking()
        
        try {
            val data = getData(id)
            
            if (data == null) {
                if (acceptHeader?.contains("application/json") == true) {
                    call.respond(HttpStatusCode.NotFound, mapOf("error" to "Resource not found"))
                } else {
                    // Use the invalidEntryPage for HTML responses
                    call.respondText(
                        invalidEntryPage(getEntityType(), id ?: "null"),
                        ContentType.Text.Html
                    )
                }
                return
            }
            
            // Check if JSON is requested
            if (acceptHeader?.contains("application/json") == true) {
                call.respond(data)
            } else {
                // Default to HTML response
                call.respondText(renderHtml(data), ContentType.Text.Html)
            }
        } finally {
            // Clear tracking after request is complete
            SparqlConnector.clearTracking()
        }
    }
    
    /**
     * Get the entity type for error messages
     * 
     * @return The entity type name
     */
    fun getEntityType(): String

    /**
     * Utility function to register route for this controller
     * 
     * @param routing The Ktor routing instance
     * @param path The route path pattern (should include {id} parameter)
     */
    fun registerRoute(routing: Routing, path: String) {
        routing.get(path) {
            handleRequest(call, call.parameters["id"])
        }
    }
}