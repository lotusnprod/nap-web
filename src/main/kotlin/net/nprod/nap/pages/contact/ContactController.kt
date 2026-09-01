package net.nprod.nap.pages.contact

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

/**
 * Controller for the contact page
 * Handles requests to the /contact route
 */
class ContactController {
    /**
     * Handle GET request to /contact
     * Renders the contact page
     *
     * @param call The application call
     */
    suspend fun handleRequest(call: ApplicationCall) {
        val data = ContactViewData()
        val html = ContactView.render(data)
        call.respondText(html, ContentType.Text.Html)
    }

    companion object {
        /**
         * Register routes for the contact controller
         *
         * @param routing The routing configuration to add routes to
         */
        fun registerRoutes(routing: Routing) {
            val controller = ContactController()
            routing.get("/contact") {
                controller.handleRequest(call)
            }
        }
    }
}
