package net.nprod.nap.pages

import defaultPage
import genURI
import kotlinx.html.*
import net.nprod.nap.pages.invalidEntryPage
import net.nprod.nap.pages.presentPharmacyResults
import net.nprod.nap.rdf.SparqlConnector
import net.nprod.nap.rdf.pharmaciesOfExtract
import net.nprod.nap.types.*

fun extractPage(identifier: String?): String {
    if (identifier == null)
        return invalidEntryPage("extract", "null")

    // Generate URI
    val uri = genURI("extract", identifier)

    val sparqlConnector = SparqlConnector()

    // Try to get the extract
    val extract = Extract.Cache[uri] ?: return invalidEntryPage("extract", identifier)
    
    // Get all pharmacies (experiments) related to this extract
    val pharmacyResults = pharmaciesOfExtract(uri, sparqlConnector)

    return defaultPage("${extract.name} (${extract.code})") {
        id = "content-node"
        div("container") {
            div("row") {
                div("col-12") {
                    h1(classes = "mt-4 mb-4") { +"Extract: ${extract.name} (${extract.code})" }
                }
            }
            
            div("row") {
                div("col-12") {
                    div("card mb-4") {
                        div("card-header bg-primary text-white") {
                            h3(classes = "card-title mb-0") { +"Experiments (${pharmacyResults.size} results)" }
                        }
                        div("card-body p-0") {
                            presentPharmacyResults(pharmacyResults, sourceType = "extract")
                        }
                    }
                }
            }
        }
    }
}