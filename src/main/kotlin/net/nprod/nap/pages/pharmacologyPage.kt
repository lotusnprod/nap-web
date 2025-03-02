import kotlinx.html.*
import net.nprod.nap.pages.invalidEntryPage
import net.nprod.nap.pages.presentPharmacyResults
import net.nprod.nap.rdf.SparqlConnector
import net.nprod.nap.rdf.pharmaciesOfPharmacology
import net.nprod.nap.types.*

fun pharmacologyPage(identifier: String?): String {
    if (identifier?.toIntOrNull() == null)
        return invalidEntryPage("pharmacology", identifier ?: "null")

    // Generate URI
    val uri = genURI("pharmacology", identifier)

    val sparqlConnector = SparqlConnector()

    // Nodes going out
    val pharmacology = Pharmacology.fromSparql(sparqlConnector, uri)
    val pharmacyResults = pharmaciesOfPharmacology(uri, sparqlConnector)


    return defaultPage("${pharmacology.name} ($identifier)") {
        id = "content-node"
        div("container") {
            div("row") {
                div("col-12") {
                    h1(classes = "mt-4 mb-4") { +"${pharmacology.name} ($identifier)" }
                }
            }
            
            div("row") {
                div("col-12") {
                    div("card mb-4") {
                        div("card-header bg-primary text-white") {
                            h3(classes = "card-title mb-0") { +"Experiments (${pharmacyResults.size} results)" }
                        }
                        div("card-body p-0") {
                            presentPharmacyResults(pharmacyResults, sourceType = "pharmacology")
                        }
                    }
                }
            }
        }
    }
}
