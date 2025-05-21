package net.nprod.nap.pages.compoundCode

import io.ktor.server.routing.*
import net.nprod.nap.pages.AbstractController
import net.nprod.nap.rdf.SparqlConnector
import net.nprod.nap.rdf.compoundsByCompoundCode
import net.nprod.nap.types.CompoundCode

/**
 * Controller for handling CompoundCode data retrieval and presentation
 */
class CompoundCodeController : AbstractController<CompoundCodeViewData>() {
    /**
     * Create the data object from the entity URI and related data
     * 
     * @param identifier The entity identifier
     * @param sparqlConnector The SPARQL connector to use for queries
     * @param uri The entity URI
     * @return The data object or null if not found
     */
    override fun createData(identifier: String, sparqlConnector: SparqlConnector, uri: String): CompoundCodeViewData? {
        val compoundCode = CompoundCode.fromSparql(uri, sparqlConnector) ?: return null
        val compounds = compoundsByCompoundCode(uri, sparqlConnector)
        
        return CompoundCodeViewData(
            identifier = identifier,
            compoundCode = compoundCode,
            compounds = compounds
        )
    }

    /**
     * Get the view that handles HTML rendering for this controller
     * 
     * @return A function that renders the data as HTML
     */
    override fun getView(): (CompoundCodeViewData) -> String = CompoundCodeView::render

    /**
     * Get the entity type for error messages
     * 
     * @return The entity type name
     */
    override fun getEntityType(): String = "compoundcode"

    /**
     * Register compound code routes
     */
    companion object {
        fun registerRoutes(routing: Routing) {
            val controller = CompoundCodeController()
            controller.registerRoute(routing, "/compoundcode/{id}")
        }
    }
}