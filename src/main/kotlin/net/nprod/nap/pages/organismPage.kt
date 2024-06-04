package net.nprod.nap.pages

import capitalize
import dataPage
import genURI
import generatePharmacyTaxaSearchURL
import getRef
import kotlinx.html.*
import net.nprod.nap.rdf.SparqlConnector
import net.nprod.nap.types.Organism

fun organismPage(identifier: String?): String {
    if (identifier?.toIntOrNull() == null)
        return invalidEntryPage("organism", identifier ?: "null")

    // Generate URI
    val uri = genURI("organism", identifier)

    val sparqlConnector = SparqlConnector()

    val organism = Organism.fromSparql(sparqlConnector, uri)

    // Nodes going in
    val inNodes = sparqlConnector.subjectAndPredicatesOf(uri)

    val family = organism.familyname?.let {
        if (it != "") "($it)" else null
    } ?: ""
    val title =
        "Organism $identifier - ${organism.genusname?.lowercase()?.capitalize()} ${organism.speciesname?.lowercase()} $family "
    return dataPage(title) {
        id = "content-node"
        h1 { +title }
        +"Organisms in Nap refer to specimens, they are not \"taxonomical\" organisms and they can in some cases even be mixtures of organisms."

        br

        a {
            href = generatePharmacyTaxaSearchURL(organism.familyname, organism.genusname, organism.speciesname)
            +"Search for all experiments from taxa with this name (matching family, genus, species)"
        }

        div {
            table(classes = "table table-striped table-bordered") {
                tbody {
                    organism.familyname?.let {
                        tr { td { +"Family" }; td { +it } }
                    }
                    organism.genusname?.let {
                        tr { td { +"Genus" }; td { +it } }
                    }
                    organism.speciesname?.let {
                        tr { td { +"Species" }; td { +it } }
                    }
                    organism.speciesAuthority?.let {
                        tr { td { +"Species authority" }; td { +it } }
                    }
                    organism.subSpeciesName?.let {
                        tr { td { +"Subspecies" }; td { +it } }
                    }
                    organism.subSpeciesAuthority?.let {
                        tr { td { +"Subspecies authority" }; td { +it } }
                    }
                    organism.comment?.let {
                        tr { td { +"Comment" }; td { +it } }
                    }
                    organism.organismClass?.let {
                        tr { td { +"Class" }; td { a(href = it.uri) { +it.name } } }
                    }
                    organism.collectedPart?.let {
                        tr { td { +"Collected part" }; td { a(href= it.uri) { +it.name } } }
                    }
                    organism.collectedCondition?.let {
                        tr { td { +"Collected condition" }; td { a(href=it.uri) { +it.name } } }
                    }
                    organism.citation?.let {
                        tr { td { +"Citation" }; td { a(href = it.uri) { +it.uri.getRef() } } }
                    }
                    organism.geographicalArea?.let {
                        tr { td { +"Geographical area" }; td { a(href=it.uri) { +it.name } } }
                    }
                    organism.country?.let {
                        tr { td { +"Country" }; td { a(href=it.uri) { +it.name } } }
                    }
                }

            }
        }

        if (inNodes.isNotEmpty()) {
            h2 { +"In nodes" }
            div { presentInNodes(inNodes) }
        }
    }
}

