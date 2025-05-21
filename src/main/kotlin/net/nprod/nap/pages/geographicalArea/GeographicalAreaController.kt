package net.nprod.nap.pages.geographicalArea

import io.ktor.server.routing.*
import net.nprod.nap.pages.AbstractController
import net.nprod.nap.rdf.SparqlConnector
import net.nprod.nap.rdf.pharmaciesOfGeographicalArea
import net.nprod.nap.types.GeographicalArea

/**
 * Controller for handling GeographicalArea data retrieval and presentation
 */
class GeographicalAreaController : AbstractController<GeographicalAreaViewData>() {
    /**
     * Create the data object from the entity URI and related data
     * 
     * @param identifier The entity identifier
     * @param sparqlConnector The SPARQL connector to use for queries
     * @param uri The entity URI
     * @return The data object or null if not found
     */
    override fun createData(identifier: String, sparqlConnector: SparqlConnector, uri: String): GeographicalAreaViewData? {
        val geographicalArea = GeographicalArea.fromSparql(sparqlConnector, uri) ?: return null
        val pharmacies = pharmaciesOfGeographicalArea(uri, sparqlConnector)
        
        return GeographicalAreaViewData(
            identifier = identifier,
            geographicalArea = geographicalArea,
            pharmacies = pharmacies
        )
    }

    /**
     * Get the view that handles HTML rendering for this controller
     * 
     * @return A function that renders the data as HTML
     */
    override fun getView(): (GeographicalAreaViewData) -> String = GeographicalAreaView::render

    /**
     * Get the entity type for error messages
     * 
     * @return The entity type name
     */
    override fun getEntityType(): String = "geographicalarea"

    /**
     * Register geographical area routes
     */
    companion object {
        fun registerRoutes(routing: Routing) {
            val controller = GeographicalAreaController()
            controller.registerRoute(routing, "/geographicalarea/{id}")
        }
    }
}