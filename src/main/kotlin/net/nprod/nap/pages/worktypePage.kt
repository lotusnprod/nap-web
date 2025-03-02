package net.nprod.nap.pages

import as_local_link_if_dev
import defaultPage
import genURI
import kotlinx.html.*
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
        h1 { +"Worktype ${worktype.code} - ${worktype.name}" }

        // Basic worktype information
        div {
            table(classes = "table table-striped table-bordered") {
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
                                a(href = it.uri.as_local_link_if_dev) { 
                                    +"${it.code} - ${it.name}" 
                                } 
                            } 
                        }
                    }
                }
            }
        }

        h2 { +"Experiments using this worktype (${pharmacyResults.size} results)" }

        // Present the pharmacy results in a table
        presentPharmacyResults(pharmacyResults, sourceType = "worktype")
    }
}