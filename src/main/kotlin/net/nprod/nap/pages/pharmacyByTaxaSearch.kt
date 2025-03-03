package net.nprod.nap.pages

import defaultPage
import kotlinx.html.*
import net.nprod.nap.rdf.SparqlConnector
import net.nprod.nap.rdf.pharmaciesOfTaxa

val safeNameRegex = Regex("[^a-zA-Z-0-9.-]")

fun pharmacyByTaxaSearch(taxonId: String): String {
    if (taxonId.contains(safeNameRegex))
        return invalidEntryPage("taxon", taxonId)

    val sparqlConnector = SparqlConnector()

    val pharmacyResults = pharmaciesOfTaxa(sparqlConnector, taxonId)


    val name = pharmacyResults.firstOrNull()?.organism?.nameForHumans() ?: taxonId
    val title = "Experiments for taxon $name"

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
                            if (pharmacyResults.size >= 10000) {
                                h3(classes = "card-title mb-0") { +"Too many results" }
                            } else {
                                h3(classes = "card-title mb-0") { +"Experiments (${pharmacyResults.size} results)" }
                            }
                        }
                        div("card-body") {
                            if (pharmacyResults.size >= 10000) {
                                div(classes = "alert alert-warning") {
                                    +"We found more than 10000 experiments. Please refine your search or use SPARQL queries."
                                }
                            }
                            
                            div("p-0") {
                                presentPharmacyResults(pharmacyResults, sourceType = "organism")
                            }
                        }
                    }
                }
            }
        }
    }
}

