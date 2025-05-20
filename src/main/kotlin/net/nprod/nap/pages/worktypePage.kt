package net.nprod.nap.pages

import defaultPage
import genURI
import kotlinx.html.*
import net.nprod.nap.helpers.localLinks
import net.nprod.nap.rdf.SparqlConnector
import net.nprod.nap.rdf.pharmaciesOfWorktype
import net.nprod.nap.types.Worktype

fun worktypePage(identifier: String?): String {
    if (identifier?.toIntOrNull() == null)
        return invalidEntryPage("worktype", identifier ?: "null")

    // Generate URI
    val uri = genURI("worktype", identifier)

    val sparqlConnector = SparqlConnector()

    // Fetch the Worktype object - note that it comes from the Cache
    val worktype = Worktype.Cache[uri] ?: return invalidEntryPage("worktype", identifier)

    // Get pharmacy results that use this worktype
    val pharmacyResults = pharmaciesOfWorktype(uri, sparqlConnector)

    return defaultPage("Worktype ${worktype.code} - ${worktype.name}") {
        id = "content-node"
        div("container") {
            div("row") {
                div("col-12") {
                    h1(classes = "mt-4 mb-3") { +"Worktype ${worktype.code} - ${worktype.name}" }
                }
            }
            
            div("row") {
                div("col-12") {
                    div("card mb-4") {
                        div("card-header bg-success text-white") {
                            h3(classes = "card-title mb-0") { +"Worktype Details" }
                        }
                        div("card-body") {
                            div("table-responsive") {
                                table(classes = "table table-striped table-bordered table-hover") {
                                    tbody {
                                        tr { 
                                            td { +"Code" }
                                            td { +worktype.code } 
                                        }
                                        tr { 
                                            td { +"Name" }
                                            td { +worktype.name } 
                                        }
                                        worktype.group?.let {
                                            tr { 
                                                td { +"Group" }
                                                td { 
                                                    a(href = localLinks(it.uri)) {
                                                        +"${it.code} - ${it.name}" 
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
            
            div("row") {
                div("col-12") {
                    div("card mb-4") {
                        div("card-header bg-primary text-white") {
                            h3(classes = "card-title mb-0") { +"Experiments (${pharmacyResults.size} results)" }
                        }
                        div("card-body p-0") {
                            presentPharmacyResults(pharmacyResults, sourceType = "worktype")
                        }
                    }
                }
            }
        }
    }
}