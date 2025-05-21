package net.nprod.nap.pages.administrationRoute

import io.ktor.server.routing.*
import net.nprod.nap.pages.AbstractController
import net.nprod.nap.rdf.SparqlConnector
import net.nprod.nap.rdf.pharmaciesOfAdministrationRoute
import net.nprod.nap.types.AdministrationRoute

/**
 * Controller for handling Administration Route data retrieval and presentation
 */
class AdministrationRouteController : AbstractController<AdministrationRouteViewData>() {
    /**
     * Create the data object from the entity URI and related data
     * 
     * @param identifier The entity identifier
     * @param sparqlConnector The SPARQL connector to use for queries
     * @param uri The entity URI
     * @return The data object or null if not found
     */
    override fun createData(identifier: String, sparqlConnector: SparqlConnector, uri: String): AdministrationRouteViewData? {
        val administrationRoute = AdministrationRoute.Cache[uri] ?: return null
        val pharmacyResults = pharmaciesOfAdministrationRoute(uri, sparqlConnector)
        
        return AdministrationRouteViewData(
            identifier = identifier,
            administrationRoute = administrationRoute,
            pharmacyResults = pharmacyResults
        )
    }

    /**
     * Get the view that handles HTML rendering for this controller
     * 
     * @return A function that renders the data as HTML
     */
    override fun getView(): (AdministrationRouteViewData) -> String = AdministrationRouteView::render

    /**
     * Get the entity type for error messages
     * 
     * @return The entity type name
     */
    override fun getEntityType(): String = "administrationroute"

    /**
     * Register administration route routes
     */
    companion object {
        fun registerRoutes(routing: Routing) {
            val controller = AdministrationRouteController()
            controller.registerRoute(routing, "/administrationroute/{id}")
        }
    }
}