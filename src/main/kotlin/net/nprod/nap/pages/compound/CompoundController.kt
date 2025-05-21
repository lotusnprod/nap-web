package net.nprod.nap.pages.compound

import io.ktor.server.routing.*
import net.nprod.nap.pages.AbstractController
import net.nprod.nap.rdf.SparqlConnector
import net.nprod.nap.rdf.pharmaciesOfCompound
import net.nprod.nap.types.Compound

/**
 * Controller for handling Compound data retrieval and presentation
 */
class CompoundController : AbstractController<CompoundViewData>() {
    /**
     * Create the data object from the entity URI and related data
     * 
     * @param identifier The entity identifier
     * @param sparqlConnector The SPARQL connector to use for queries
     * @param uri The entity URI
     * @return The data object or null if not found
     */
    override fun createData(identifier: String, sparqlConnector: SparqlConnector, uri: String): CompoundViewData {
        val compound = Compound.fromSparql(sparqlConnector, uri)
        val pharmacyResults = pharmaciesOfCompound(uri, sparqlConnector)
        
        return CompoundViewData(
            identifier = identifier,
            compound = compound,
            pharmacies = pharmacyResults
        )
    }

    /**
     * Get the view that handles HTML rendering for this controller
     * 
     * @return A function that renders the data as HTML
     */
    override fun getView(): (CompoundViewData) -> String = CompoundView::render

    /**
     * Get the entity type for error messages
     * 
     * @return The entity type name
     */
    override fun getEntityType(): String = "compound"

    /**
     * Register compound routes
     */
    companion object {
        fun registerRoutes(routing: Routing) {
            val controller = CompoundController()
            controller.registerRoute(routing, "/compound/{id}")
        }
    }
}