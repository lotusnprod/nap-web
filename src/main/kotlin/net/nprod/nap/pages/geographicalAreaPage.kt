import kotlinx.html.*
import net.nprod.nap.helpers.localLinks
import net.nprod.nap.pages.invalidEntryPage
import net.nprod.nap.pages.presentPharmacyResults
import net.nprod.nap.rdf.SparqlConnector
import net.nprod.nap.rdf.pharmaciesOfGeographicalArea
import net.nprod.nap.types.GeographicalArea

/**
 * A page that displays a geographical area.
 */
fun geographicalAreaPage(identifier: String?): String {
    if (identifier?.toIntOrNull() == null)
        return invalidEntryPage("geographicalarea", identifier ?: "null")

    // Generate URI
    val uri = genURI("geographicalarea", identifier)
    val sparqlConnector = SparqlConnector()

    // We get the data about that geographical area
    val geographicalArea = GeographicalArea.fromSparql(sparqlConnector, uri)
        ?: return invalidEntryPage("geographicalarea", identifier)

    val pharmacies = pharmaciesOfGeographicalArea(uri, sparqlConnector)

    return defaultPage("Geographical Area: ${geographicalArea.name}") {
        id = "content-node"
        div("container") {
            div("row") {
                div("col-12") {
                    h1(classes = "mt-4 mb-4") { +"Geographical Area: ${geographicalArea.name}" }
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
                                        td { +geographicalArea.name }
                                    }
                                    tr {
                                        th { +"URI" }
                                        td { a(href = localLinks(geographicalArea.uri)) { +geographicalArea.uri } }
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
                                h3(classes = "card-title mb-0") { +"Experiments in this geographical area or with organisms from this area (${pharmacies.size} results)" }
                            }
                            div("card-body p-0") {
                                presentPharmacyResults(pharmacies, sourceType = "geographicalarea")
                            }
                        }
                    }
                }
            }
        }
    }
}