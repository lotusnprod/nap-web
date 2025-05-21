package net.nprod.nap.pages.compoundSearch

import kotlinx.html.*
import net.nprod.nap.helpers.localLinks
import net.nprod.nap.pages.genericSearchPage
import org.apache.jena.query.ResultSet

/**
 * View for the compound search page
 * Renders the search interface and results using the genericSearchPage template
 */
object CompoundSearchView {
    /**
     * Render the compound search page
     * @param data The view data containing search query and results
     * @return HTML string of the search page
     */
    fun render(data: CompoundSearchViewData): String {
        return genericSearchPage(
            entityType = "Compound",
            query = data.query,
            searchQueryFunction = null, // Not used directly in the view
            processResults = { _: ResultSet -> emptyList() }, // Not used directly in the view
            renderTableHeaders = {
                th { +"Name" }
                th { +"Compound Class" }
            },
            renderTableRow = { compound ->
                td { 
                    val uri = compound["uri"]!!
                    a(href = localLinks(uri)) {
                        +compound["name"]!!
                    }
                }
                td { +compound["compoundClass"]!! }
            },
            preProcessedResults = data.compounds // Use pre-processed results
        )
    }
}