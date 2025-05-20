package net.nprod.nap.pages

import defaultPage
import genURI
import kotlinx.html.*
import net.nprod.nap.helpers.localLinks
import net.nprod.nap.rdf.SparqlConnector
import net.nprod.nap.rdf.compoundsOfSynonym
import net.nprod.nap.rdf.getSynonymDetails

fun compoundSynonymPage(identifier: String?): String {
    if (identifier?.toIntOrNull() == null)
        return invalidEntryPage("compoundsynonym", identifier ?: "null")

    // Generate URI
    val uri = genURI("compoundsynonym", identifier)

    val sparqlConnector = SparqlConnector()

    // Get synonym details
    val synonym = getSynonymDetails(uri, sparqlConnector) 
        ?: return invalidEntryPage("compoundsynonym", identifier)

    // Get compounds that have this synonym
    val compounds = compoundsOfSynonym(uri, sparqlConnector)

    return defaultPage("Compound Synonym: ${synonym.name} ($identifier)") {
        id = "content-node"
        div("container") {
            div("row") {
                div("col-12") {
                    h1(classes = "mt-4 mb-4") { +"Compound Synonym: ${synonym.name} ($identifier)" }
                }
            }
            
            div("row") {
                div("col-8") {
                    div("card mb-4") {
                        div("card-header bg-success text-white") {
                            h3(classes = "card-title mb-0") { +"Synonym Details" }
                        }
                        div("card-body") {
                            div("table-responsive") {
                                table(classes = "table table-striped table-hover") {
                                    tbody {
                                        tr { 
                                            th(scope = ThScope.row, classes = "w-25") { +"Name" }
                                            td { +synonym.name } 
                                        }
                                        tr { 
                                            th(scope = ThScope.row) { +"URI" }
                                            td { +uri } 
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                
                div("col-4") {
                    div("card mb-4") {
                        div("card-header bg-secondary text-white") {
                            h3(classes = "card-title mb-0") { +"Summary" }
                        }
                        div("card-body") {
                            p { +"Associated Compounds: ${compounds.size}" }
                        }
                    }
                }
            }
            
            div("row") {
                div("col-12") {
                    div("card mb-4") {
                        id = "compounds"
                        div("card-header bg-primary text-white") {
                            h3(classes = "card-title mb-0") { 
                                +"Associated Compounds (${compounds.size})" 
                            }
                        }
                        div("card-body p-0") {
                            if (compounds.isEmpty()) {
                                div("p-3") {
                                    +"No compounds found associated with this synonym."
                                }
                            } else {
                                div("list-group list-group-flush") {
                                    compounds.forEach { compound ->
                                        a(href = localLinks(compound.uri), 
                                          classes = "list-group-item list-group-item-action d-flex justify-content-between align-items-center") {
                                            div {
                                                span("fw-bold") { +(compound.name ?: "Unnamed") }
                                                compound.compoundClass?.let { compoundClass ->
                                                    span("ms-2 badge bg-secondary") { +compoundClass }
                                                }
                                            }
                                            span("badge bg-primary rounded-pill") { +"view" }
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