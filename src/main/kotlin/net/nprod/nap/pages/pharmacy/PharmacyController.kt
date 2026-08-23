package net.nprod.nap.pages.pharmacy

import io.ktor.server.routing.*
import net.nprod.nap.pages.AbstractController
import net.nprod.nap.rdf.SparqlConnector
import net.nprod.nap.types.Pharmacy

/**
 * Controller for handling Pharmacy data retrieval and presentation
 */
class PharmacyController : AbstractController<PharmacyViewData>() {
    /**
     * Create the data object from the entity URI and related data
     * 
     * @param identifier The entity identifier
     * @param sparqlConnector The SPARQL connector to use for queries
     * @param uri The entity URI
     * @return The data object or null if not found
     */
    override fun createData(identifier: String, sparqlConnector: SparqlConnector, uri: String): PharmacyViewData {
        // No blanket catch: a missing pharmacy throws EntityNotFoundException (404)
        // and a backend failure throws SparqlUnavailableException (503). Swallowing
        // both into null made an outage look like a bad id, with nothing in the log.
        val pharmacy = Pharmacy.fromSparql(sparqlConnector, uri)
        return PharmacyViewData(
            identifier = identifier,
            pharmacy = pharmacy
        )
    }

    /**
     * Get the view that handles HTML rendering for this controller
     * 
     * @return A function that renders the data as HTML
     */
    override fun getView(): (PharmacyViewData) -> String = PharmacyView::render

    /**
     * Get the entity type for error messages
     * 
     * @return The entity type name
     */
    override fun getEntityType(): String = "pharmacy"

    /**
     * Register pharmacy routes
     */
    companion object {
        fun registerRoutes(routing: Routing) {
            val controller = PharmacyController()
            controller.registerRoute(routing, "/pharmacy/{id}")
        }
    }
}