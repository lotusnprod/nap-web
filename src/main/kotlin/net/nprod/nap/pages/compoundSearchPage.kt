package net.nprod.nap.pages

import kotlinx.html.*
import net.nprod.nap.rdf.SparqlConnector
import defaultPage
import io.ktor.util.toLowerCasePreservingASCIIRules
import net.nprod.nap.helpers.localLinks

/**
 * Search for compounds by name
 * @param query The search query
 * @return HTML page with search results
 */
fun compoundSearchPage(query: String?): String {
    if (query.isNullOrBlank()) {
        return defaultPage("Compound Search") {
            div("container") {
                div("row") {
                    div("col-12") {
                        h1(classes = "mt-4 mb-4") { +"Compound Search" }
                        p { +"Please enter a search term." }
                    }
                }
            }
        }
    }

    val sparqlConnector = SparqlConnector()

    val cleanQuery = query.replace("\\", "\\\\").replace("\"", "\\\"").toLowerCasePreservingASCIIRules();
    // Search for compounds by name
    val searchQuery = """
        PREFIX n: <https://nap.nprod.net/>
        PREFIX text: <http://jena.apache.org/text#>
        SELECT DISTINCT ?compound ?name ?compoundClass ?number
        WHERE {
            ?compound text:query (n:name "$cleanQuery").
            ?compound a n:compound;
                      n:name ?name;
                      n:compoundclass ?compoundClass;
                      n:number ?number.
        }
        ORDER BY ?name
    """.trimIndent()

    val results = sparqlConnector.getResultsOfQuery(searchQuery)
    val compounds = mutableListOf<Map<String, String>>()

    if (results != null) {
        while (results.hasNext()) {
            val solution = results.nextSolution()
            val compoundUri = solution["compound"].asResource().uri
            val name = solution["name"].asLiteral().string
            val compoundClass = solution["compoundClass"]?.asLiteral()?.string ?: "Unknown"
            val number = solution["number"]?.asLiteral()?.int?.toString() ?: "Unknown"

            compounds.add(mapOf(
                "uri" to compoundUri,
                "name" to name,
                "compoundClass" to compoundClass,
                "number" to number
            ))
        }
    }

    return defaultPage("Compound Search: $query") {
        div("container") {
            div("row") {
                div("col-12") {
                    h1(classes = "mt-4 mb-4") { +"Compound Search: $query" }
                    p { +"Found ${compounds.size} results." }
                }
            }

            div("row") {
                div("col-12") {
                    div("card mb-4") {
                        div("card-header bg-success text-white") {
                            h3(classes = "card-title mb-0") { +"Search Results" }
                        }
                        div("card-body p-0") {
                            if (compounds.isEmpty()) {
                                div("p-3") {
                                    p { +"No compounds found matching '$query'." }
                                }
                            } else {
                                div("table-responsive") {
                                    table(classes = "table table-striped table-hover") {
                                        thead {
                                            tr {
                                                th { +"Name" }
                                                th { +"Compound Class" }
                                            }
                                        }
                                        tbody {
                                            compounds.forEach { compound ->
                                                tr {
                                                    td { 
                                                        val uri = compound["uri"]!!
                                                        a(href = localLinks(uri)) {
                                                            +compound["name"]!!
                                                        }
                                                    }
                                                    td { +compound["compoundClass"]!! }
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
