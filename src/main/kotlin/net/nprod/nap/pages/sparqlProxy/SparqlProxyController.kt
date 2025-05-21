package net.nprod.nap.pages.sparqlProxy

import io.ktor.client.statement.bodyAsText
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

/**
 * Controller for handling SPARQL proxy requests
 * This controller handles both GET and POST requests to /raw/sparql
 */
class SparqlProxyController {
    // Create a single instance of the service
    private val sparqlProxyService = SparqlProxyService()
    
    /**
     * Handle POST request to /raw/sparql
     * 
     * @param call The ApplicationCall to handle
     */
    suspend fun handlePostRequest(call: ApplicationCall) {
        val body = call.receiveText()
        val response = sparqlProxyService.handlePostRequest(body)
        call.respondText(response.bodyAsText(), response.contentType())
    }
    
    /**
     * Handle GET request to /raw/sparql
     * 
     * @param call The ApplicationCall to handle
     */
    suspend fun handleGetRequest(call: ApplicationCall) {
        val response = sparqlProxyService.handleGetRequest(call.request.queryParameters)
        call.respondText(response.bodyAsText(), response.contentType())
    }
    
    /**
     * Register SPARQL proxy routes
     */
    companion object {
        fun registerRoutes(routing: Routing) {
            val controller = SparqlProxyController()
            
            // Register POST route
            routing.post("/raw/sparql") {
                controller.handlePostRequest(call)
            }
            
            // Register GET route
            routing.get("/raw/sparql") {
                controller.handleGetRequest(call)
            }
        }
    }
}