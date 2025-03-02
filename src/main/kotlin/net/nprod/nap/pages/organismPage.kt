package net.nprod.nap.pages

import as_local_link_if_dev
import capitalize
import defaultPage
import genURI
import generatePharmacyTaxaSearchURL
import getRef
import kotlinx.html.*
import net.nprod.nap.rdf.SparqlConnector
import net.nprod.nap.rdf.pharmaciesOfOrganism
import net.nprod.nap.rdf.pharmaciesOfPharmacology
import net.nprod.nap.types.Organism

fun organismPage(identifier: String?): String {
    if (identifier?.toIntOrNull() == null)
        return invalidEntryPage("organism", identifier ?: "null")

    // Generate URI
    val uri = genURI("organism", identifier)

    val sparqlConnector = SparqlConnector()

    val organism = Organism.fromSparql(sparqlConnector, uri)


    val pharmacyResults = pharmaciesOfOrganism(uri, sparqlConnector)

    val family = organism.familyname?.let {
        if (it != "") "($it)" else null
    } ?: ""
    val title =
        "Organism $identifier - ${organism.genusname?.lowercase()?.capitalize()} ${organism.speciesname?.lowercase()} $family "
    return defaultPage(title) {
        id = "content-node"
        div("container") {
            div("row") {
                div("col-12") {
                    h1(classes = "mt-4 mb-3") { +title }
                    div("alert alert-info") {
                        +"Organisms in Nap refer to specimens, they are not \"taxonomical\" organisms and they can in some cases even be mixtures of organisms. Empty organisms are used as placeholders as the original dataset entry system used organism as the central part."
                    }
                    
                    organism.taxon?.let { taxon ->
                        div("my-3") {
                            a(classes = "btn btn-primary") {
                                href = generatePharmacyTaxaSearchURL(taxon)
                                +"Search for all experiments on this taxon"
                            }
                        }
                    }
                }
            }
            
            div("row") {
                div("col-12") {
                    div("card mb-4") {
                        div("card-header bg-success text-white") {
                            h3(classes = "card-title mb-0") { +"Organism Details" }
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
                                        organism.taxon?.let {
                                            tr { td { +"Taxon" }; td { a(href= it.as_local_link_if_dev) { +it.getRef() } } }
                                        }
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
                                            tr { td { +"Class" }; td { a(href = it.uri.as_local_link_if_dev) { +it.name } } }
                                        }
                                        organism.collectedPart?.let {
                                            tr { td { +"Collected part" }; td { a(href= it.uri.as_local_link_if_dev) { +it.name } } }
                                        }
                                        organism.partCondition?.let {
                                            tr { td { +"Collected condition" }; td { a(href=it.uri.as_local_link_if_dev) { +it.name } } }
                                        }
                                        organism.citation?.let {
                                            tr { td { +"Citation" }; td { a(href = it.uri.as_local_link_if_dev) { +it.uri.as_local_link_if_dev.getRef() } } }
                                        }
                                        organism.geographicalArea?.let {
                                            tr { td { +"Geographical area" }; td { a(href=it.uri.as_local_link_if_dev) { +it.name } } }
                                        }
                                        organism.country?.let {
                                            tr { td { +"Country" }; td { a(href=it.uri.as_local_link_if_dev) { +it.name } } }
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
                            presentPharmacyResults(pharmacyResults, sourceType = "pharmacology")
                        }
                    }
                }
            }
        }
    }
}

