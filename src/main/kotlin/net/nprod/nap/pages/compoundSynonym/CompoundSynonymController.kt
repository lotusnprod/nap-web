package net.nprod.nap.pages.compoundSynonym

import io.ktor.server.routing.*
import net.nprod.nap.pages.AbstractController
import net.nprod.nap.rdf.SparqlConnector
import net.nprod.nap.rdf.compoundsOfSynonym
import net.nprod.nap.rdf.getSynonymDetails

/**
 * Controller for handling CompoundSynonym data retrieval and presentation
 */
class CompoundSynonymController : AbstractController<CompoundSynonymViewData>() {
    /**
     * Create the data object from the entity URI and related data
     * 
     * @param identifier The entity identifier
     * @param sparqlConnector The SPARQL connector to use for queries
     * @param uri The entity URI
     * @return The data object or null if not found
     */
    override fun createData(identifier: String, sparqlConnector: SparqlConnector, uri: String): CompoundSynonymViewData? {
        val synonym = getSynonymDetails(uri, sparqlConnector) ?: return null
        val compounds = compoundsOfSynonym(uri, sparqlConnector)
        
        return CompoundSynonymViewData(
            identifier = identifier,
            synonym = synonym,
            compounds = compounds
        )
    }

    /**
     * Get the view that handles HTML rendering for this controller
     * 
     * @return A function that renders the data as HTML
     */
    override fun getView(): (CompoundSynonymViewData) -> String = CompoundSynonymView::render

    /**
     * Get the entity type for error messages
     * 
     * @return The entity type name
     */
    override fun getEntityType(): String = "compoundsynonym"

    /**
     * Register compound synonym routes
     */
    companion object {
        fun registerRoutes(routing: Routing) {
            val controller = CompoundSynonymController()
            controller.registerRoute(routing, "/compoundsynonym/{id}")
        }
    }
}