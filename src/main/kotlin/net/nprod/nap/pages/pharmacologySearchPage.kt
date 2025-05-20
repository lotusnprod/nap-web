package net.nprod.nap.pages

import kotlinx.html.*
import net.nprod.nap.rdf.SparqlConnector
import defaultPage
import net.nprod.nap.helpers.localLinks
import net.nprod.nap.rdf.pharmacologySearchQuery

/**
 * Search for pharmacology entries by name
 * @param query The search query
 * @return HTML page with search results
 */
fun pharmacologySearchPage(query: String?): String {
    if (query.isNullOrBlank()) {
        return defaultPage("Pharmacology Search") {
            div("container") {
                div("row") {
                    div("col-12") {
                        h1(classes = "mt-4 mb-4") { +"Pharmacology Search" }
                        p { +"Please enter a search term." }
                    }
                }
            }
        }
    }

    val sparqlConnector = SparqlConnector()
    
    // Get SPARQL query for pharmacology search
    val searchQuery = pharmacologySearchQuery(query)

    val results = sparqlConnector.getResultsOfQuery(searchQuery)
    val pharmacologyEntries = mutableListOf<Map<String, String>>()

    if (results != null) {
        while (results.hasNext()) {
            val solution = results.nextSolution()
            val pharmacologyUri = solution["pharmacology"].asResource().uri
            val name = solution["name"].asLiteral().string

            pharmacologyEntries.add(mapOf(
                "uri" to pharmacologyUri,
                "name" to name
            ))
        }
    }

    return defaultPage("Pharmacology Search: $query") {
        div("container") {
            div("row") {
                div("col-12") {
                    h1(classes = "mt-4 mb-4") { +"Pharmacology Search: $query" }
                    p { +"Found ${pharmacologyEntries.size} results." }
                }
            }

            div("row") {
                div("col-12") {
                    div("card mb-4") {
                        div("card-header bg-primary text-white") {
                            h3(classes = "card-title mb-0") { +"Search Results" }
                        }
                        div("card-body p-0") {
                            if (pharmacologyEntries.isEmpty()) {
                                div("p-3") {
                                    p { +"No pharmacology entries found matching '$query'." }
                                }
                            } else {
                                div("table-responsive") {
                                    table(classes = "table table-striped table-hover") {
                                        thead {
                                            tr {
                                                th { +"Name" }
                                            }
                                        }
                                        tbody {
                                            pharmacologyEntries.forEach { entry ->
                                                tr {
                                                    td { 
                                                        val uri = entry["uri"]!!
                                                        a(href = localLinks(uri)) { +entry["name"]!! }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
