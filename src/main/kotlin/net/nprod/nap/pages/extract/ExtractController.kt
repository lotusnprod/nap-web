package net.nprod.nap.pages.extract

import io.ktor.server.routing.*
import net.nprod.nap.pages.AbstractController
import net.nprod.nap.rdf.SparqlConnector
import net.nprod.nap.rdf.pharmaciesOfExtract
import net.nprod.nap.types.Extract

/**
 * Controller for handling Extract data retrieval and presentation
 */
class ExtractController : AbstractController<ExtractViewData>() {
    /**
     * Create the data object from the entity URI and related data
     * 
     * @param identifier The entity identifier
     * @param sparqlConnector The SPARQL connector to use for queries
     * @param uri The entity URI
     * @return The data object or null if not found
     */
    override fun createData(identifier: String, sparqlConnector: SparqlConnector, uri: String): ExtractViewData? {
        val extract = Extract.Cache[uri] ?: return null
        val pharmacyResults = pharmaciesOfExtract(uri, sparqlConnector)
        
        return ExtractViewData(
            identifier = identifier,
            extract = extract,
            pharmacyResults = pharmacyResults
        )
    }

    /**
     * Get the view that handles HTML rendering for this controller
     * 
     * @return A function that renders the data as HTML
     */
    override fun getView(): (ExtractViewData) -> String = ExtractView::render

    /**
     * Get the entity type for error messages
     * 
     * @return The entity type name
     */
    override fun getEntityType(): String = "extract"

    /**
     * Register extract routes
     */
    companion object {
        fun registerRoutes(routing: Routing) {
            val controller = ExtractController()
            controller.registerRoute(routing, "/extract/{id}")
        }
    }
}