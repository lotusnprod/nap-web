package net.nprod.nap.pages.organism

import io.ktor.server.routing.*
import net.nprod.nap.pages.AbstractController
import net.nprod.nap.rdf.SparqlConnector
import net.nprod.nap.rdf.pharmaciesOfOrganism
import net.nprod.nap.types.Organism
import genURI

/**
 * Controller for handling Organism data retrieval and presentation
 */
class OrganismController : AbstractController<OrganismViewData>() {
    /**
     * Create the data object from the entity URI and related data
     * 
     * @param identifier The entity identifier
     * @param sparqlConnector The SPARQL connector to use for queries
     * @param uri The entity URI
     * @return The data object or null if not found
     */
    override fun createData(identifier: String, sparqlConnector: SparqlConnector, uri: String): OrganismViewData? {
        try {
            val organism = Organism.fromSparql(sparqlConnector, uri)
            val pharmacyResults = pharmaciesOfOrganism(uri, sparqlConnector)
                .sortedBy { it.number?.toIntOrNull() ?: Int.MAX_VALUE }
            
            return OrganismViewData(
                identifier = identifier,
                organism = organism,
                pharmacyResults = pharmacyResults
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
    override fun getView(): (OrganismViewData) -> String = OrganismView::render

    /**
     * Get the entity type for error messages
     * 
     * @return The entity type name
     */
    override fun getEntityType(): String = "organism"

    /**
     * Register organism routes
     */
    companion object {
        fun registerRoutes(routing: Routing) {
            val controller = OrganismController()
            controller.registerRoute(routing, "/organism/{id}")
        }
    }
}