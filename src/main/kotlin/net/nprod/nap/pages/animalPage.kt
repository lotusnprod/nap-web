package net.nprod.nap.pages

import defaultPage
import genURI
import kotlinx.html.*
import net.nprod.nap.rdf.SparqlConnector
import net.nprod.nap.rdf.pharmaciesOfAnimal
import net.nprod.nap.types.Animal

fun animalPage(identifier: String?): String {
    if (identifier?.toIntOrNull() == null)
        return invalidEntryPage("animal", identifier ?: "null")

    // Generate URI
    val uri = genURI("animal", identifier)

    val sparqlConnector = SparqlConnector()

    // Get animal from cache
    val animal = Animal.Cache[uri] ?: return invalidEntryPage("animal", identifier)
    
    // Get experiments that involve this animal
    val pharmacyResults = pharmaciesOfAnimal(uri, sparqlConnector)
    
    val title = "Animal: ${animal.name} ($identifier)"
    
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
                        div("card-header bg-success text-white") {
                            h3(classes = "card-title mb-0") { +"Animal Details" }
                        }
                        div("card-body") {
                            div("table-responsive") {
                                table(classes = "table table-striped table-bordered table-hover") {
                                    thead {
                                        tr {
                                            th { +"Property" }
                                            th { +"Value" }
                                        }
                                    }
                                    tbody {
                                        tr { 
                                            td { +"Name" }
                                            td { +animal.name }
                                        }
                                        tr {
                                            td { +"URI" }
                                            td { +uri }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            
            if (pharmacyResults.isNotEmpty()) {
                div("row") {
                    div("col-12") {
                        div("card mb-4") {
                            div("card-header bg-primary text-white") {
                                h3(classes = "card-title mb-0") { +"Experiments" }
                            }
                            div("card-body p-0") {
                                presentPharmacyResults(pharmacyResults, sourceType = "animal")
                            }
                        }
                    }
                }
            }
        }
    }
}