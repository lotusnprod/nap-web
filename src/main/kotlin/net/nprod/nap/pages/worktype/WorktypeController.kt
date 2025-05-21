package net.nprod.nap.pages.worktype

import io.ktor.server.routing.*
import net.nprod.nap.pages.AbstractController
import net.nprod.nap.rdf.SparqlConnector
import net.nprod.nap.rdf.pharmaciesOfWorktype
import net.nprod.nap.types.Worktype

/**
 * Controller for handling Worktype data retrieval and presentation
 */
class WorktypeController : AbstractController<WorktypeViewData>() {
    /**
     * Create the data object from the entity URI and related data
     * 
     * @param identifier The entity identifier
     * @param sparqlConnector The SPARQL connector to use for queries
     * @param uri The entity URI
     * @return The data object or null if not found
     */
    override fun createData(identifier: String, sparqlConnector: SparqlConnector, uri: String): WorktypeViewData? {
        val worktype = Worktype.Cache[uri] ?: return null
        val pharmacyResults = pharmaciesOfWorktype(uri, sparqlConnector)
        
        return WorktypeViewData(
            identifier = identifier,
            worktype = worktype,
            pharmacyResults = pharmacyResults
        )
    }

    /**
     * Get the view that handles HTML rendering for this controller
     * 
     * @return A function that renders the data as HTML
     */
    override fun getView(): (WorktypeViewData) -> String = WorktypeView::render

    /**
     * Get the entity type for error messages
     * 
     * @return The entity type name
     */
    override fun getEntityType(): String = "worktype"

    /**
     * Register worktype routes
     */
    companion object {
        fun registerRoutes(routing: Routing) {
            val controller = WorktypeController()
            controller.registerRoute(routing, "/worktype/{id}")
        }
    }
}