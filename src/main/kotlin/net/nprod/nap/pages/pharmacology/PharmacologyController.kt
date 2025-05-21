package net.nprod.nap.pages.pharmacology

import io.ktor.server.routing.*
import net.nprod.nap.pages.AbstractController
import net.nprod.nap.rdf.SparqlConnector
import net.nprod.nap.rdf.pharmaciesOfPharmacology
import net.nprod.nap.types.Pharmacology
import genURI

/**
 * Controller for handling Pharmacology data retrieval and presentation
 */
class PharmacologyController : AbstractController<PharmacologyViewData>() {
    /**
     * Create the data object from the entity URI and related data
     * 
     * @param identifier The entity identifier
     * @param sparqlConnector The SPARQL connector to use for queries
     * @param uri The entity URI
     * @return The data object or null if not found
     */
    override fun createData(identifier: String, sparqlConnector: SparqlConnector, uri: String): PharmacologyViewData? {
        try {
            val pharmacology = Pharmacology.fromSparql(sparqlConnector, uri)
            val pharmacyResults = pharmaciesOfPharmacology(uri, sparqlConnector)
            
            return PharmacologyViewData(
                identifier = identifier,
                pharmacology = pharmacology,
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
    override fun getView(): (PharmacologyViewData) -> String = PharmacologyView::render

    /**
     * Get the entity type for error messages
     * 
     * @return The entity type name
     */
    override fun getEntityType(): String = "pharmacology"

    /**
     * Register pharmacology routes
     */
    companion object {
        fun registerRoutes(routing: Routing) {
            val controller = PharmacologyController()
            controller.registerRoute(routing, "/pharmacology/{id}")
        }
    }
}