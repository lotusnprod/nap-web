package net.nprod.nap.pages.animal

import kotlinx.html.*
import net.nprod.nap.pages.defaultPage
import net.nprod.nap.pages.presentPharmacyResults

/**
 * View for rendering animal page
 */
object AnimalView {
    /**
     * Renders the animal page
     * 
     * @param data The animal data to render
     * @return HTML string representation of the page
     */
    fun render(data: AnimalViewData): String {
        val animal = data.animal
        val identifier = data.identifier
        val pharmacyResults = data.pharmacyResults
        
        val title = "Animal: ${animal.name} ($identifier)"
        
        return defaultPage(title) {
            id = "content-node"
            div("container") {
                div("row") {
                    div("col-12") {
                        h1(classes = "mt-4 mb-4") { +title }
                    }
                }
                
                div("row") {
                    div("col-12") {
                        div("card mb-4") {
                            div("card-header bg-success text-white") {
                                h3(classes = "card-title mb-0") { +"Animal Details" }
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
                                            tr { 
                                                td { +"Name" }
                                                td { +animal.name }
                                            }
                                            tr {
                                                td { +"URI" }
                                                td { +animal.uri }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                
                if (pharmacyResults.isNotEmpty()) {
                    div("row") {
                        div("col-12") {
                            div("card mb-4") {
                                div("card-header bg-primary text-white") {
                                    h3(classes = "card-title mb-0") { +"Experiments (${pharmacyResults.size})" }
                                }
                                div("card-body p-0") {
                                    presentPharmacyResults(pharmacyResults, sourceType = "animal")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}