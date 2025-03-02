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
                div("col-12") {
                    div("card mb-4") {
                        div("card-header bg-success text-white") {
                            h3(classes = "card-title mb-0") { +"Compound Details" }
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
                                        compound.name?.let {
                                            tr { td { +"Name" }; td { +it } }
                                        }
                                        compound.compoundClass?.let {
                                            tr { td { +"Compound class" }; td { +it } }
                                        }
                                        compound.compoundCode?.let {
                                            tr { td { +"Compound code" }; td { a(href = it.uri.as_local_link_if_dev.as_local_link_if_dev) { +it.name } } }
                                        }
                                        if (compound.synonyms.isNotEmpty()) {
                                            tr {
                                                td { +"Synonyms" }
                                                td {
                                                    div("list-group") {
                                                        compound.synonyms.forEach { synonym ->
                                                            a(href = synonym.uri.as_local_link_if_dev.as_local_link_if_dev, classes = "list-group-item list-group-item-action") {
                                                                +synonym.name
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
            
            div("row") {
                div("col-12") {
                    div("card mb-4") {
                        div("card-header bg-primary text-white") {
                            h3(classes = "card-title mb-0") { +"Experiments" }
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
