package net.nprod.nap.pages.organism

import kotlinx.html.*
import net.nprod.nap.helpers.localLinks
import net.nprod.nap.pages.defaultPage
import net.nprod.nap.pages.presentPharmacyResults
import generatePharmacyTaxaSearchURL
import getRef

/**
 * View for rendering organism page
 */
object OrganismView {
    /**
     * Renders the organism page
     * 
     * @param data The organism data to render
     * @return HTML string representation of the page
     */
    fun render(data: OrganismViewData): String {
        val organism = data.organism
        val pharmacyResults = data.pharmacyResults
        
        val family = organism.familyname?.let {
            if (it != "") "($it)" else null
        } ?: ""
        
        val title = "Organism ${data.identifier} - ${organism.genusname?.lowercase()?.capitalize()} ${organism.speciesname?.lowercase()} $family "
        
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
                                                tr { td { +"Taxon" }; td { a(href= localLinks(it)) { +it.getRef() } } }
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
                                                tr { td { +"Class" }; td { a(href = localLinks(it.uri)) { +it.name } } }
                                            }
                                            organism.collectedPart?.let {
                                                tr { td { +"Collected part" }; td { a(href= localLinks(it.uri)) { +it.name } } }
                                            }
                                            organism.partCondition?.let {
                                                tr { td { +"Collected condition" }; td { a(href=localLinks(it.uri)) { +it.name } } }
                                            }
                                            organism.citation?.let {
                                                tr { td { +"Citation" }; td { a(href = localLinks(it.uri)) { +it.formatCitation() } } }
                                            }
                                            organism.geographicalArea?.let {
                                                tr { td { +"Geographical area" }; td { a(href=localLinks(it.uri)) { +it.name } } }
                                            }
                                            organism.country?.let {
                                                tr { td { +"Country" }; td { a(href=localLinks(it.uri)) { +it.name } } }
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
}