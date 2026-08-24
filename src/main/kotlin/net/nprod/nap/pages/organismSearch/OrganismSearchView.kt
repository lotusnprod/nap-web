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
                th { +"Genus" }
                th { +"Species" }
                th { +"Subspecies" }
                th { +"Family" }
                th { +"Type" }
                th(classes = "text-end") { +"Experiments" }
            },
            renderTableRow = { organism ->
                val genus = organism["genusname"].orEmpty()
                val species = organism["speciesname"].orEmpty()
                val subspecies = organism["subspeciesname"].orEmpty()
                val href = destinationOf(organism)

                // The link goes on the most specific rank the record actually names, so
                // every row has exactly one thing to click even when there is no species,
                // and the cell that carries it is the cell that identifies the row.
                val linkedGenus = species.isBlank()

                td { if (linkedGenus) a(href = href) { +genusOrFallback(organism) } else +genusOrFallback(organism) }
                td {
                    if (!linkedGenus) a(href = href) { +species }

                    // The destination is a taxon page, and a taxon is named after one
                    // epithet alone — the species for a species, the subspecies for a
                    // subspecies. Both are already columns here, so this only has
                    // something to add when the taxon was reconciled to a third name.
                    val taxonName = organism["taxonName"].orEmpty()
                    val alreadyShown = taxonName.equals(species, ignoreCase = true) ||
                        taxonName.equals(subspecies, ignoreCase = true)
                    if (taxonName.isNotBlank() && !alreadyShown) {
                        div("text-muted small") { +"taxon: $taxonName" }
                    }
                }
                td { +subspecies }
                td { +organism["familyname"].orEmpty() }
                td { +organism["organismClass"].orEmpty() }
                td(classes = "text-end") { +organism["experiments"].orEmpty() }
            },
            preProcessedResults = data.organisms, // Use pre-processed results
            searchPath = "/organism/search"
        )
    }

    /**
     * Where a result row leads: the experiments recorded for its taxon
     *
     * @param organism One row of the search results
     * @return The taxon's experiment list, or the organism record itself when the row has
     *   no taxon to stand for
     */
    private fun destinationOf(organism: Map<String, String>): String {
        val taxon = organism["taxon"].orEmpty()
        if (taxon.isEmpty()) return localLinks(organism["uri"].orEmpty())
        return "/pharmacy_search?taxon_id=${taxon.split("/").last()}"
    }

    /**
     * What the genus cell says
     *
     * Some organism records have no name at all — the original entry system created one
     * for every citation whether or not there was an organism to describe — so the cell
     * falls back to the record's number rather than being blank and unclickable.
     *
     * @param organism One row of the search results
     * @return The genus, or a stand-in naming the record
     */
    private fun genusOrFallback(organism: Map<String, String>): String =
        organism["genusname"].orEmpty().ifBlank { organism["recordedName"].orEmpty() }
}