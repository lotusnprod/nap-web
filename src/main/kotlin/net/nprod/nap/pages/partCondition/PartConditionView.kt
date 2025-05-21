package net.nprod.nap.pages.partCondition

import kotlinx.html.*
import net.nprod.nap.helpers.localLinks
import net.nprod.nap.pages.defaultPage
import net.nprod.nap.pages.presentPharmacyResults

/**
 * View for rendering part condition page
 */
object PartConditionView {
    /**
     * Renders the part condition page
     * 
     * @param data The part condition data to render
     * @return HTML string representation of the page
     */
    fun render(data: PartConditionViewData): String {
        val partCondition = data.partCondition
        val pharmacies = data.pharmacies
        
        return defaultPage("Collected Condition: ${partCondition.name}") {
            id = "content-node"
            div("container") {
                div("row") {
                    div("col-12") {
                        h1(classes = "mt-4 mb-4") { +"Collected Condition: ${partCondition.name}" }
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
                                            td { +partCondition.name }
                                        }
                                        tr {
                                            th { +"URI" }
                                            td { a(href = localLinks(partCondition.uri)) { +partCondition.uri } }
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
                                    h3(classes = "card-title mb-0") { +"Experiments with this collected condition (${pharmacies.size} results)" }
                                }
                                div("card-body p-0") {
                                    presentPharmacyResults(pharmacies, sourceType = "partcondition")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}