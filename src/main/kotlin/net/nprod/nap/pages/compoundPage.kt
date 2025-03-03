import kotlinx.html.*
import net.nprod.nap.pages.invalidEntryPage
import net.nprod.nap.pages.presentPharmacyResults
import net.nprod.nap.rdf.SparqlConnector
import net.nprod.nap.rdf.pharmaciesOfCompound
import net.nprod.nap.types.*

fun compoundPage(identifier: String?): String {
    if (identifier?.toIntOrNull() == null)
        return invalidEntryPage("compound", identifier ?: "null")

    // Generate URI
    val uri = genURI("compound", identifier)

    val sparqlConnector = SparqlConnector()

    // Nodes going out
    val compound = Compound.fromSparql(sparqlConnector, uri)
    val pharmacyResults = pharmaciesOfCompound(uri, sparqlConnector)

    return defaultPage("${compound.name} ($identifier)") {
        id = "content-node"
        div("container") {
            div("row") {
                div("col-12") {
                    h1(classes = "mt-4 mb-4") { +"${compound.name} ($identifier)" }
                }
            }
            
            div("row") {
                div("col-8") {
                    div("card mb-4") {
                        div("card-header bg-success text-white") {
                            h3(classes = "card-title mb-0") { +"Compound Details" }
                        }
                        div("card-body") {
                            div("table-responsive") {
                                table(classes = "table table-striped table-hover") {
                                    tbody {
                                        compound.name?.let {
                                            tr { 
                                                th(scope = ThScope.row, classes = "w-25") { +"Name" }
                                                td { +it } 
                                            }
                                        }
                                        compound.compoundClass?.let {
                                            tr { 
                                                th(scope = ThScope.row) { +"Compound class" }
                                                td { +it } 
                                            }
                                        }
                                        compound.publicationCode?.let {
                                            tr { 
                                                th(scope = ThScope.row) { +"Publication code" }
                                                td { +it } 
                                            }
                                        }
                                        compound.publicationName?.let {
                                            tr { 
                                                th(scope = ThScope.row) { +"Publication name" }
                                                td { +it } 
                                            }
                                        }
                                        compound.compoundCode?.let {
                                            tr { 
                                                th(scope = ThScope.row) { +"Compound code" }
                                                td { a(href = it.uri.as_local_link_if_dev) { +it.name } } 
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    
                    if (compound.synonyms.isNotEmpty()) {
                        div("card mb-4") {
                            div("card-header bg-info text-white") {
                                h3(classes = "card-title mb-0") { +"Synonyms" }
                            }
                            div("card-body p-0") {
                                div("list-group list-group-flush") {
                                    compound.synonyms.forEach { synonym ->
                                        a(href = synonym.uri.as_local_link_if_dev, classes = "list-group-item list-group-item-action d-flex justify-content-between align-items-center") {
                                            +synonym.name
                                            span("badge bg-primary rounded-pill") { +"view" }
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
                            p { +"Compound class: ${compound.compoundClass ?: "Unknown"}" }
                            p { +"Experiments: ${pharmacyResults.size}" }
                            hr {}
                            div("d-grid gap-2") {
                                a(href = "#experiments", classes = "btn btn-primary") {
                                    +"View Experiments"
                                }
                            }
                        }
                    }
                }
            }
            
            div("row") {
                div("col-12") {
                    div("card mb-4") {
                        id = "experiments"
                        div("card-header bg-primary text-white") {
                            h3(classes = "card-title mb-0") { 
                                +"Experiments (${pharmacyResults.size})" 
                            }
                        }
                        div("card-body p-0") {
                            presentPharmacyResults(pharmacyResults, sourceType = "compound")
                        }
                    }
                }
            }
        }
    }
}