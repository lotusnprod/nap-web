package net.nprod.nap.pages.animal

import io.ktor.server.routing.*
import net.nprod.nap.pages.AbstractController
import net.nprod.nap.rdf.SparqlConnector
import net.nprod.nap.rdf.pharmaciesOfAnimal
import net.nprod.nap.types.Animal

/**
 * Controller for handling Animal data retrieval and presentation
 */
class AnimalController : AbstractController<AnimalViewData>() {
    /**
     * Create the data object from the entity URI and related data
     * 
     * @param identifier The entity identifier
     * @param sparqlConnector The SPARQL connector to use for queries
     * @param uri The entity URI
     * @return The data object or null if not found
     */
    override fun createData(identifier: String, sparqlConnector: SparqlConnector, uri: String): AnimalViewData? {
        val animal = Animal.Cache[uri] ?: return null
        val pharmacyResults = pharmaciesOfAnimal(uri, sparqlConnector)
        
        return AnimalViewData(
            identifier = identifier,
            animal = animal,
            pharmacyResults = pharmacyResults
        )
    }

    /**
     * Get the view that handles HTML rendering for this controller
     * 
     * @return A function that renders the data as HTML
     */
    override fun getView(): (AnimalViewData) -> String = AnimalView::render

    /**
     * Get the entity type for error messages
     * 
     * @return The entity type name
     */
    override fun getEntityType(): String = "animal"

    /**
     * Register animal routes
     */
    companion object {
        fun registerRoutes(routing: Routing) {
            val controller = AnimalController()
            controller.registerRoute(routing, "/animal/{id}")
        }
    }
}