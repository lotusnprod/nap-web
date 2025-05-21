package net.nprod.nap.pages.pharmacyByTaxaSearch

import kotlinx.html.*
import net.nprod.nap.pages.defaultPage
import net.nprod.nap.pages.presentPharmacyResults

/**
 * View for the pharmacy by taxa search page
 * Renders the search results for experiments related to a specific taxon
 */
object PharmacyByTaxaSearchView {
    /**
     * Render the pharmacy by taxa search page
     * @param data The view data containing taxon ID and experiment results
     * @return HTML string of the search page
     */
    fun render(data: PharmacyByTaxaSearchViewData): String {
        val title = "Experiments for taxon ${data.organismName}"

        return defaultPage(title) {
            id = "content-node"
            div("container") {
                div("row") {
                    div("col-12") {
                        h1(classes = "mt-4 mb-4") { +title }
                    }
                }
                
                div("row") {
                    div("col-12") {
                        div("card mb-4") {
                            div("card-header bg-primary text-white") {
                                if (data.pharmacyResults.size >= 10000) {
                                    h3(classes = "card-title mb-0") { +"Too many results" }
                                } else {
                                    h3(classes = "card-title mb-0") { +"Experiments (${data.pharmacyResults.size} results)" }
                                }
                            }
                            div("card-body") {
                                if (data.pharmacyResults.size >= 10000) {
                                    div(classes = "alert alert-warning") {
                                        +"We found more than 10000 experiments. Please refine your search or use SPARQL queries."
                                    }
                                }
                                
                                div("p-0") {
                                    presentPharmacyResults(data.pharmacyResults, sourceType = "organism")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}