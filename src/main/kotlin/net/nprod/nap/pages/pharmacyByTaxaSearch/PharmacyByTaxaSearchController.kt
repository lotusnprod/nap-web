package net.nprod.nap.pages.pharmacyByTaxaSearch

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import net.nprod.nap.pages.invalidEntry.InvalidEntryUtil
import net.nprod.nap.rdf.SparqlConnector
import net.nprod.nap.rdf.pharmaciesOfTaxa
import net.nprod.nap.rdf.taxonName
import net.nprod.nap.types.Pharmacy

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
            // A rejected taxon id is a malformed request, not a page that exists.
            call.respondText(
                InvalidEntryUtil.createInvalidEntryPage("taxon", taxonId),
                ContentType.Text.Html,
                HttpStatusCode.BadRequest
            )
            return
        }

        val pharmacyResults = pharmaciesOfTaxa(sparqlConnector, taxonId)

        val data = PharmacyByTaxaSearchViewData(
            taxonId = taxonId,
            pharmacyResults = pharmacyResults,
            taxonName = headingFor(taxonId, pharmacyResults)
        )
        
        val html = PharmacyByTaxaSearchView.render(data)
        call.respondText(html, ContentType.Text.Html)
    }

    /**
     * What to call the taxon at the top of the page
     *
     * Not the taxon's own n:name: only the species and subspecies levels were ever
     * populated, so that name is a bare epithet — "ALBA" — and a page headed by it says
     * nothing. The organism records carry the whole name, and no taxon in the store spans
     * two genera, so the name they agree on is the taxon's name spelled out. Taking the
     * most frequent one rather than the first keeps a single odd record from renaming the
     * page, which is what made this heading wrong before.
     *
     * @param taxonId The numeric identifier of the taxon, the last resort
     * @param pharmacyResults The experiments recorded for it
     * @return A name for the taxon
     */
    private fun headingFor(taxonId: String, pharmacyResults: List<Pharmacy>): String {
        val names = pharmacyResults.mapNotNull { it.organism?.nameForHumans() }
            .filter { it.isNotBlank() && it != "Empty organism" }
        val consensus = names.groupingBy { it }.eachCount().maxByOrNull { it.value }?.key
        return consensus ?: taxonName(sparqlConnector, taxonId) ?: taxonId
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