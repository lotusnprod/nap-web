package net.nprod.nap.pages.geographicalArea

import kotlinx.html.*
import net.nprod.nap.helpers.localLinks
import net.nprod.nap.pages.defaultPage
import net.nprod.nap.pages.presentPharmacyResults

/**
 * View for rendering geographical area page
 */
object GeographicalAreaView {
    /**
     * Renders the geographical area page
     * 
     * @param data The geographical area data to render
     * @return HTML string representation of the page
     */
    fun render(data: GeographicalAreaViewData): String {
        val geographicalArea = data.geographicalArea
        val pharmacies = data.pharmacies
        
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
}