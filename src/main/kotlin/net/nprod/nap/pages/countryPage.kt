import as_local_link_if_dev
import defaultPage
import genURI
import getRef
import kotlinx.html.*
import net.nprod.nap.pages.invalidEntryPage
import net.nprod.nap.pages.presentPharmacyResults
import net.nprod.nap.rdf.SparqlConnector
import net.nprod.nap.rdf.pharmaciesOfCountry
import net.nprod.nap.types.Country

/**
 * A page that displays a country.
 */
fun countryPage(identifier: String?): String {
    if (identifier?.toIntOrNull() == null)
        return invalidEntryPage("country", identifier ?: "null")

    // Generate URI
    val uri = genURI("country", identifier)
    val sparqlConnector = SparqlConnector()

    // We get the data about that country
    val country = Country.fromSparql(sparqlConnector, uri)
        ?: return invalidEntryPage("country", identifier)

    val pharmacies = pharmaciesOfCountry(uri, sparqlConnector)

    return defaultPage("Country: ${country.name}") {
        id = "content-node"
        div("container") {
            div("row") {
                div("col-12") {
                    h1(classes = "mt-4 mb-4") { +"Country: ${country.name}" }
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
                                        td { +country.name }
                                    }
                                    tr {
                                        th { +"URI" }
                                        td { a(href = country.uri.as_local_link_if_dev) { +country.uri } }
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
                                h3(classes = "card-title mb-0") { +"Experiments in this country or with organisms from this country (${pharmacies.size} results)" }
                            }
                            div("card-body p-0") {
                                presentPharmacyResults(pharmacies, sourceType = "country")
                            }
                        }
                    }
                }
            }
        }
    }
}