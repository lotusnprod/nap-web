package net.nprod.nap.pages.pharmacologySearch

import kotlinx.html.*
import net.nprod.nap.helpers.localLinks
import net.nprod.nap.pages.SearchListing
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
                th { +"Activity" }
                th(classes = "text-end") { +"Experiments" }
            },
            renderTableRow = { entry ->
                td {
                    val uri = entry["uri"]!!
                    a(href = localLinks(uri)) { +entry["name"]!! }
                }
                // An activity nothing was recorded for is still a real entry of the
                // vocabulary; the count is what says there is nothing behind it.
                td(classes = "text-end") { +(entry["experiments"] ?: "") }
            },
            headerColor = "bg-primary",
            preProcessedResults = data.pharmacologyEntries, // Use pre-processed results
            searchPath = "/pharmacology/search",
            listing = SearchListing(
                title = "Pharmacological activities",
                intro = "Every activity recorded in NAP, by name. Following one lists the experiments that " +
                    "measured it, whatever the organism or the compound involved.",
                // Matching the whole row would let a count digit hide half the vocabulary
                filterTextOf = { it["name"] ?: "" }
            )
        )
    }
}
