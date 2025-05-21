package net.nprod.nap.pages

import kotlinx.html.*
import net.nprod.nap.rdf.SparqlConnector
import net.nprod.nap.pages.defaultPage
import net.nprod.nap.helpers.localLinks
import org.apache.jena.query.ResultSet

/**
 * Generic search page template that can be used for different entity types
 * 
 * @param entityType The type of entity being searched (used for display purposes)
 * @param query The search query string
 * @param searchQueryFunction A function that generates the SPARQL query for the search (can be null if preProcessedResults is provided)
 * @param processResults A function that processes the search results into a list of maps (can be null if preProcessedResults is provided)
 * @param renderTableHeaders A function that renders the table headers in the result table
 * @param renderTableRow A function that renders a row in the result table for a single entity
 * @param headerColor The color of the card header (default: "bg-success")
 * @param additionalInfoBlock Optional function to render additional info about the results
 * @param preProcessedResults Optional pre-processed results to use instead of querying the database
 * @return HTML page with search results
 */
fun genericSearchPage(
    entityType: String,
    query: String?,
    searchQueryFunction: ((String) -> String)?,
    processResults: ((ResultSet) -> List<Map<String, String>>)?,
    renderTableHeaders: TR.() -> Unit,
    renderTableRow: TR.(Map<String, String>) -> Unit,
    headerColor: String = "bg-success",
    additionalInfoBlock: (DIV.(List<Map<String, String>>) -> Unit)? = null,
    preProcessedResults: List<Map<String, String>>? = null
): String {
    if (query.isNullOrBlank()) {
        return defaultPage("$entityType Search") {
            div("container") {
                div("row") {
                    div("col-12") {
                        h1(classes = "mt-4 mb-4") { +"$entityType Search" }
                        p { +"Please enter a search term." }
                    }
                }
            }
        }
    }

    // Use pre-processed results if provided, otherwise query the database
    val entities = preProcessedResults ?: if (searchQueryFunction != null && processResults != null) {
        val sparqlConnector = SparqlConnector()
        
        // Get SPARQL query for entity search
        val searchQuery = searchQueryFunction(query)

        val results = sparqlConnector.getResultsOfQuery(searchQuery)
        if (results != null) processResults(results) else emptyList()
    } else {
        emptyList()
    }

    return defaultPage("$entityType Search: $query") {
        div("container") {
            div("row") {
                div("col-12") {
                    h1(classes = "mt-4 mb-4") { +"$entityType Search: $query" }
                    p { +"Found ${entities.size} results." }
                    if (additionalInfoBlock != null) {
                        additionalInfoBlock(this, entities)
                    }
                }
            }

            div("row") {
                div("col-12") {
                    div("card mb-4") {
                        div("card-header $headerColor text-white") {
                            h3(classes = "card-title mb-0") { +"Search Results" }
                        }
                        div("card-body p-0") {
                            if (entities.isEmpty()) {
                                div("p-3") {
                                    p { +"No ${entityType.lowercase()} found matching '$query'." }
                                }
                            } else {
                                div("table-responsive") {
                                    table(classes = "table table-striped table-hover") {
                                        thead {
                                            tr {
                                                renderTableHeaders()
                                            }
                                        }
                                        tbody {
                                            entities.forEach { entity ->
                                                tr {
                                                    renderTableRow(entity)
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