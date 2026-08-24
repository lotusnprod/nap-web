package net.nprod.nap.pages.organismSearch

import kotlinx.html.*
import net.nprod.nap.helpers.localLinks
import net.nprod.nap.pages.genericSearchPage
import org.apache.jena.query.ResultSet

/**
 * View for the organism search page
 * Renders the search interface and results using the genericSearchPage template
 */
object OrganismSearchView {
    /**
     * Render the organism search page
     * @param data The view data containing search query and results
     * @return HTML string of the search page
     */
    fun render(data: OrganismSearchViewData): String {
        return genericSearchPage(
            // Results are grouped by taxon, one row per taxon, so this is a taxon search
            // even though it matches on organism names.
            entityType = "Taxon",
            query = data.query,
            searchQueryFunction = null, // Not used directly in the view
            processResults = { _: ResultSet -> emptyList() }, // Not used directly in the view
            renderTableHeaders = {
                th { +"Name" }
                th { +"Family" }
                th { +"Subspecies" }
            },
            renderTableRow = { organism ->
                td {
                    val displayName = organism["displayName"]!!
                    val taxon = organism["taxon"]!!
                    if (taxon.isNotEmpty()) {
                        val taxonId = taxon.split("/").last()
                        a(href = "/pharmacy_search?taxon_id=$taxonId") { +displayName }
                    } else {
                        val uri = organism["uri"]!!
                        a(href = localLinks(uri)) { +displayName }
                    }

                    // The row is named after the taxon, but the match was on an organism
                    // record. When the two names differ, saying so is the difference
                    // between a reconciled name and an apparently unrelated one.
                    val recordedName = organism["recordedName"] ?: ""
                    if (recordedName.isNotEmpty() && recordedName != displayName) {
                        div("text-muted small") { +"recorded as $recordedName" }
                    }
                }
                td { +organism["familyname"]!! }
                td { +organism["subspeciesname"]!! }
            },
            preProcessedResults = data.organisms // Use pre-processed results
        )
    }
}