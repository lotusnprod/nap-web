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


    return dataPage("${compound.name} ($identifier)") {
        id = "content-node"
        h1 { +"${compound.name} ($identifier)" }
        h2 { +"Details" }

        div {
            table(classes = "table table-striped table-bordered") {
                tbody {
                    compound.name?.let {
                        tr { td { +"Name" }; td { +it } }
                    }
                    compound.compoundClass?.let {
                        tr { td { +"Compound class" }; td { +it } }
                    }
                    compound.compoundCode?.let {
                        tr { td { +"Compound code" }; td { a(href = it.uri) { +it.name } } }
                    }
                    if (compound.synonyms.isNotEmpty()) {
                        tr {
                            td { +"Synonym" }
                            td {
                                ul {
                                    compound.synonyms.forEach { synonym ->
                                        li { a(href = synonym.uri) { +synonym.name } }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }


        h2 { +"Works" }

        presentPharmacyResults(pharmacyResults, sourceType = "compound")
    }
}
