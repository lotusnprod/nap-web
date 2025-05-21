package net.nprod.nap.pages.country

import io.ktor.server.routing.*
import net.nprod.nap.pages.AbstractController
import net.nprod.nap.rdf.SparqlConnector
import net.nprod.nap.rdf.pharmaciesOfCountry
import net.nprod.nap.types.Country
import genURI

/**
 * Controller for handling Country data retrieval and presentation
 */
class CountryController : AbstractController<CountryViewData>() {
    /**
     * Create the data object from the entity URI and related data
     * 
     * @param identifier The entity identifier
     * @param sparqlConnector The SPARQL connector to use for queries
     * @param uri The entity URI
     * @return The data object or null if not found
     */
    override fun createData(identifier: String, sparqlConnector: SparqlConnector, uri: String): CountryViewData? {
        val country = Country.fromSparql(sparqlConnector, uri) ?: return null
        val pharmacies = pharmaciesOfCountry(uri, sparqlConnector)
        
        return CountryViewData(
            identifier = identifier,
            country = country,
            pharmacies = pharmacies
        )
    }

    /**
     * Get the view that handles HTML rendering for this controller
     * 
     * @return A function that renders the data as HTML
     */
    override fun getView(): (CountryViewData) -> String = CountryView::render

    /**
     * Get the entity type for error messages
     * 
     * @return The entity type name
     */
    override fun getEntityType(): String = "country"

    /**
     * Register country routes
     */
    companion object {
        fun registerRoutes(routing: Routing) {
            val controller = CountryController()
            controller.registerRoute(routing, "/country/{id}")
        }
    }
}