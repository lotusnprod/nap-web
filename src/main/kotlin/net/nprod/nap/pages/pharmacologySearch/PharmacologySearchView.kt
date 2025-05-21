package net.nprod.nap.pages.pharmacologySearch

import kotlinx.html.*
import net.nprod.nap.helpers.localLinks
import net.nprod.nap.pages.genericSearchPage
import org.apache.jena.query.ResultSet

/**
 * View for the pharmacology search page
 * Renders the search interface and results using the genericSearchPage template
 */
object PharmacologySearchView {
    /**
     * Render the pharmacology search page
     * @param data The view data containing search query and results
     * @return HTML string of the search page
     */
    fun render(data: PharmacologySearchViewData): String {
        return genericSearchPage(
            entityType = "Pharmacology",
            query = data.query,
            searchQueryFunction = null, // Not used directly in the view
            processResults = { _: ResultSet -> emptyList() }, // Not used directly in the view
            renderTableHeaders = {
                th { +"Name" }
            },
            renderTableRow = { entry ->
                td { 
                    val uri = entry["uri"]!!
                    a(href = localLinks(uri)) { +entry["name"]!! }
                }
            },
            headerColor = "bg-primary",
            preProcessedResults = data.pharmacologyEntries // Use pre-processed results
        )
    }
}