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
        h1 { +"${pharmacology.name} ($identifier)" }

        h2 { +"Experiments" }

        presentPharmacyResults(pharmacyResults, sourceType = "pharmacology")
    }
}
