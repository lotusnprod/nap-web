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
                    val taxon = organism["taxon"]!!
                    if (taxon.isNotEmpty()) {
                        val taxonId = taxon.split("/").last()
                        a(href = "/pharmacy_search?taxon_id=$taxonId") { +organism["displayName"]!! }
                    } else {
                        val uri = organism["uri"]!!
                        a(href = localLinks(uri)) { +organism["displayName"]!! }
                    }
                }
                td { +organism["familyname"]!! }
                td { +organism["subspeciesname"]!! }
            },
            preProcessedResults = data.organisms // Use pre-processed results
        )
    }
}