package net.nprod.nap.pages.pharmacyByTaxaSearch

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import net.nprod.nap.pages.invalidEntry.InvalidEntryUtil
import net.nprod.nap.rdf.SparqlConnector
import net.nprod.nap.rdf.pharmaciesOfTaxa

/**
 * Controller for the pharmacy by taxa search page
 * Handles requests to search for pharmacy experiments related to a specific taxon
 */
class PharmacyByTaxaSearchController {
    private val sparqlConnector = SparqlConnector()
    private val safeNameRegex = Regex("[^a-zA-Z-0-9.-]")
    
    /**
     * Handle GET request to /pharmacy_search
     * Searches for pharmacy experiments related to the specified taxon
     *
     * @param call The application call
     */
    suspend fun handleRequest(call: ApplicationCall) {
        val taxonId = call.parameters["taxon_id"] ?: ""
        
        if (taxonId.contains(safeNameRegex)) {
            call.respondText(InvalidEntryUtil.createInvalidEntryPage("taxon", taxonId), ContentType.Text.Html)
            return
        }

        val pharmacyResults = pharmaciesOfTaxa(sparqlConnector, taxonId)
        val organismName = pharmacyResults.firstOrNull()?.organism?.nameForHumans() ?: taxonId

        val data = PharmacyByTaxaSearchViewData(
            taxonId = taxonId,
            pharmacyResults = pharmacyResults,
            organismName = organismName
        )
        
        val html = PharmacyByTaxaSearchView.render(data)
        call.respondText(html, ContentType.Text.Html)
    }

    companion object {
        /**
         * Register routes for the pharmacy by taxa search controller
         *
         * @param routing The routing configuration to add routes to
         */
        fun registerRoutes(routing: Routing) {
            val controller = PharmacyByTaxaSearchController()
            routing.get("/pharmacy_search") {
                controller.handleRequest(call)
            }
        }
    }
}