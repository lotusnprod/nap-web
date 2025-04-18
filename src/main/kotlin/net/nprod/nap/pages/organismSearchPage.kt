package net.nprod.nap.pages

import kotlinx.html.*
import net.nprod.nap.rdf.SparqlConnector
import net.nprod.nap.types.*
import defaultPage
import as_local_link_if_dev

/**
 * Search for organisms by name
 * @param query The search query
 * @return HTML page with search results
 */
fun organismSearchPage(query: String?): String {
    if (query.isNullOrBlank()) {
        return defaultPage("Organism Search") {
            div("container") {
                div("row") {
                    div("col-12") {
                        h1(classes = "mt-4 mb-4") { +"Organism Search" }
                        p { +"Please enter a search term." }
                    }
                }
            }
        }
    }

    val sparqlConnector = SparqlConnector()

    // Search for organisms by name (using genus and species names)
    val searchQuery = """
        PREFIX n: <https://nap.nprod.net/>
        SELECT ?organism ?genusname ?speciesname ?subspeciesname ?familyname ?number ?taxon
        WHERE {
            ?organism a n:organism;
                      n:number ?number.
            OPTIONAL { ?organism n:genusname ?genusname }
            OPTIONAL { ?organism n:speciesname ?speciesname }
            OPTIONAL { ?organism n:subspeciesname ?subspeciesname }
            OPTIONAL { ?organism n:familyname ?familyname }
            OPTIONAL { ?organism n:has_taxon ?taxon }

            FILTER(
                (BOUND(?genusname) && CONTAINS(LCASE(?genusname), LCASE("${query.replace("\\", "\\\\").replace("\"", "\\\"")}"))) ||
                (BOUND(?speciesname) && CONTAINS(LCASE(?speciesname), LCASE("${query.replace("\\", "\\\\").replace("\"", "\\\"")}"))) ||
                (BOUND(?subspeciesname) && CONTAINS(LCASE(?subspeciesname), LCASE("${query.replace("\\", "\\\\").replace("\"", "\\\"")}"))) ||
                (BOUND(?familyname) && CONTAINS(LCASE(?familyname), LCASE("${query.replace("\\", "\\\\").replace("\"", "\\\"")}"))
            ))
        }
        ORDER BY ?genusname ?speciesname
    """.trimIndent()

    val results = sparqlConnector.getResultsOfQuery(searchQuery)
    val organisms = mutableListOf<Map<String, String>>()

    if (results != null) {
        while (results.hasNext()) {
            val solution = results.nextSolution()
            val organismUri = solution["organism"].asResource().uri
            val genusname = solution["genusname"]?.asLiteral()?.string ?: ""
            val speciesname = solution["speciesname"]?.asLiteral()?.string ?: ""
            val subspeciesname = solution["subspeciesname"]?.asLiteral()?.string ?: ""
            val familyname = solution["familyname"]?.asLiteral()?.string ?: ""
            val number = solution["number"]?.asLiteral()?.int?.toString() ?: "Unknown"
            val taxon = solution["taxon"]?.asResource()?.uri

            val displayName = buildString {
                if (genusname.isNotBlank()) {
                    append(genusname.capitalize())
                    if (speciesname.isNotBlank()) {
                        append(" ")
                        append(speciesname.lowercase())
                        if (subspeciesname.isNotBlank()) {
                            append(" subsp. ")
                            append(subspeciesname.lowercase())
                        }
                    }
                    if (familyname.isNotBlank()) {
                        append(" (")
                        append(familyname)
                        append(")")
                    }
                } else {
                    append("Organism #$number")
                }
            }

            organisms.add(mapOf(
                "uri" to organismUri,
                "displayName" to displayName,
                "genusname" to genusname,
                "speciesname" to speciesname,
                "subspeciesname" to subspeciesname,
                "familyname" to familyname,
                "number" to number,
                "taxon" to (taxon ?: "")
            ))
        }
    }

    // Group organisms by taxon and only show the first one per taxon
    val groupedByTaxon = organisms.groupBy { it["taxon"] }
    val firstPerTaxon = groupedByTaxon
        .filter { it.key?.isNotEmpty() == true } // Filter out organisms without taxon
        .map { it.value.first() } // Take the first organism for each taxon

    return defaultPage("Organism Search: $query") {
        div("container") {
            div("row") {
                div("col-12") {
                    h1(classes = "mt-4 mb-4") { +"Organism Search: $query" }
                    p { +"Found ${organisms.size} results (${firstPerTaxon.size} unique taxa)." }
                }
            }

            div("row") {
                div("col-12") {
                    div("card mb-4") {
                        div("card-header bg-success text-white") {
                            h3(classes = "card-title mb-0") { +"Search Results" }
                        }
                        div("card-body p-0") {
                            if (organisms.isEmpty()) {
                                div("p-3") {
                                    p { +"No organisms found matching '$query'." }
                                }
                            } else {
                                div("table-responsive") {
                                    table(classes = "table table-striped table-hover") {
                                        thead {
                                            tr {
                                                th { +"Name" }
                                                th { +"Family" }
                                                th { +"Subspecies" }
                                                th { +"Taxon Search" }
                                            }
                                        }
                                        tbody {
                                            firstPerTaxon.forEach { organism ->
                                                tr {
                                                    td { 
                                                        val uri = organism["uri"]!!
                                                        a(href = uri.as_local_link_if_dev) { +organism["displayName"]!! }
                                                    }
                                                    td { +organism["familyname"]!! }
                                                    td { +organism["subspeciesname"]!! }
                                                    td {
                                                        val taxon = organism["taxon"]!!
                                                        if (taxon.isNotEmpty()) {
                                                            val taxonId = taxon.split("/").last()
                                                            a(href = "/pharmacy_search?taxon_id=$taxonId", classes = "btn btn-primary btn-sm") { +"View all experiments" }
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
}
