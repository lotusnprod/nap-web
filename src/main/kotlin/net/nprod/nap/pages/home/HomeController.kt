package net.nprod.nap.pages.home

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

/**
 * Controller for the home page
 * Handles requests to the root path
 */
class HomeController {
    /**
     * Handle GET request to /
     * Renders the home page
     *
     * @param call The application call
     */
    suspend fun handleRequest(call: ApplicationCall) {
        val data = HomeViewData()
        val html = HomeView.render(data)
        call.respondText(html, ContentType.Text.Html)
    }

    companion object {
        /**
         * Register routes for the home controller
         *
         * @param routing The routing configuration to add routes to
         */
        fun registerRoutes(routing: Routing) {
            val controller = HomeController()
            routing.get("/") {
                controller.handleRequest(call)
            }
        }
    }
}