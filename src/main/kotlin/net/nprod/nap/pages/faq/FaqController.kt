package net.nprod.nap.pages.faq

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

/**
 * Controller for the FAQ page
 * Handles requests to the /faq route
 */
class FaqController {
    /**
     * Handle GET request to /faq
     * Renders the FAQ page
     *
     * @param call The application call
     */
    suspend fun handleRequest(call: ApplicationCall) {
        val data = FaqViewData()
        val html = FaqView.render(data)
        call.respondText(html, ContentType.Text.Html)
    }

    companion object {
        /**
         * Register routes for the FAQ controller
         *
         * @param routing The routing configuration to add routes to
         */
        fun registerRoutes(routing: Routing) {
            val controller = FaqController()
            routing.get("/faq") {
                controller.handleRequest(call)
            }
        }
    }
}