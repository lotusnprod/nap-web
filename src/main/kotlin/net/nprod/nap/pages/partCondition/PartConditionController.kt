package net.nprod.nap.pages.partCondition

import io.ktor.server.routing.*
import net.nprod.nap.pages.AbstractController
import net.nprod.nap.rdf.SparqlConnector
import net.nprod.nap.rdf.pharmaciesOfPartCondition
import net.nprod.nap.types.PartCondition
import genURI

/**
 * Controller for handling Part Condition data retrieval and presentation
 */
class PartConditionController : AbstractController<PartConditionViewData>() {
    /**
     * Create the data object from the entity URI and related data
     * 
     * @param identifier The entity identifier
     * @param sparqlConnector The SPARQL connector to use for queries
     * @param uri The entity URI
     * @return The data object or null if not found
     */
    override fun createData(identifier: String, sparqlConnector: SparqlConnector, uri: String): PartConditionViewData? {
        try {
            val partCondition = PartCondition.fromSparql(sparqlConnector, uri)
            val pharmacies = pharmaciesOfPartCondition(uri, sparqlConnector)
            
            return PartConditionViewData(
                identifier = identifier,
                partCondition = partCondition,
                pharmacies = pharmacies
            )
        } catch (e: Exception) {
            return null
        }
    }

    /**
     * Get the view that handles HTML rendering for this controller
     * 
     * @return A function that renders the data as HTML
     */
    override fun getView(): (PartConditionViewData) -> String = PartConditionView::render

    /**
     * Get the entity type for error messages
     * 
     * @return The entity type name
     */
    override fun getEntityType(): String = "partcondition"

    /**
     * Register part condition routes
     */
    companion object {
        fun registerRoutes(routing: Routing) {
            val controller = PartConditionController()
            controller.registerRoute(routing, "/partcondition/{id}")
        }
    }
}