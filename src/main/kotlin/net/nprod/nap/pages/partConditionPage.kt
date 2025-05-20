import kotlinx.html.*
import net.nprod.nap.helpers.localLinks
import net.nprod.nap.pages.invalidEntryPage
import net.nprod.nap.pages.presentPharmacyResults
import net.nprod.nap.rdf.SparqlConnector
import net.nprod.nap.rdf.pharmaciesOfPartCondition
import net.nprod.nap.types.Partcondition

/**
 * A page that displays a collected condition.
 */
fun PartConditionPage(identifier: String?): String {
    if (identifier?.toIntOrNull() == null)
        return invalidEntryPage("partcondition", identifier ?: "null")

    // Generate URI
    val uri = genURI("partcondition", identifier)
    val sparqlConnector = SparqlConnector()

    // We get the data about that collected condition
    val partCondition = Partcondition.fromSparql(sparqlConnector, uri)

    val pharmacies = pharmaciesOfPartCondition(uri, sparqlConnector)

    return defaultPage("Collected Condition: ${partCondition.name}") {
        id = "content-node"
        div("container") {
            div("row") {
                div("col-12") {
                    h1(classes = "mt-4 mb-4") { +"Collected Condition: ${partCondition.name}" }
                }
            }
            div("row") {
                div("col-12") {
                    div("card mb-3") {
                        div("card-header bg-success text-white") {
                            h3(classes = "card-title mb-0") { +"Details" }
                        }
                        div("card-body") {
                            table("table table-hover") {
                                tbody {
                                    tr { 
                                        th { +"Name" }
                                        td { +partCondition.name }
                                    }
                                    tr {
                                        th { +"URI" }
                                        td { a(href = localLinks(partCondition.uri)) { +partCondition.uri } }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            if (pharmacies.isNotEmpty()) {
                div("row") {
                    div("col-12") {
                        div("card mb-4") {
                            div("card-header bg-primary text-white") {
                                h3(classes = "card-title mb-0") { +"Experiments with this collected condition (${pharmacies.size} results)" }
                            }
                            div("card-body p-0") {
                                presentPharmacyResults(pharmacies, sourceType = "partcondition")
                            }
                        }
                    }
                }
            }
        }
    }
}