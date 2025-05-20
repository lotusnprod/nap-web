package net.nprod.nap.pages

import defaultPage
import genURI
import kotlinx.html.*
import net.nprod.nap.rdf.SparqlConnector
import net.nprod.nap.rdf.pharmaciesOfAdministrationRoute
import net.nprod.nap.types.AdministrationRoute
import net.nprod.nap.helpers.capitalize

fun administrationRoutePage(identifier: String?): String {
    if (identifier == null)
        return invalidEntryPage("administrationroute", "null")

    val uri = genURI("administrationroute", identifier)

    val sparqlConnector = SparqlConnector()

    val administrationRoute = AdministrationRoute.Cache[uri] ?: return invalidEntryPage("administrationroute", identifier)
    
    val pharmacyResults = pharmaciesOfAdministrationRoute(uri, sparqlConnector)

    return defaultPage("${administrationRoute.name.capitalize()} Administration Route ($identifier)") {
        id = "content-node"
        div("container") {
            div("row") {
                div("col-12") {
                    h1(classes = "mt-4 mb-4") { +"${administrationRoute.name.capitalize()} Administration Route ($identifier)" }
                }
            }
            
            div("row") {
                div("col-12") {
                    div("card mb-4") {
                        div("card-header bg-primary text-white") {
                            h3(classes = "card-title mb-0") { +"Experiments (${pharmacyResults.size} results)" }
                        }
                        div("card-body p-0") {
                            presentPharmacyResults(pharmacyResults, sourceType = "administrationroute")
                        }
                    }
                }
            }
        }
    }
}