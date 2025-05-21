package net.nprod.nap.pages.worktype

import kotlinx.html.*
import net.nprod.nap.helpers.localLinks
import net.nprod.nap.pages.defaultPage
import net.nprod.nap.pages.presentPharmacyResults

/**
 * View for rendering worktype page
 */
object WorktypeView {
    /**
     * Renders the worktype page
     * 
     * @param data The worktype data to render
     * @return HTML string representation of the page
     */
    fun render(data: WorktypeViewData): String {
        val worktype = data.worktype
        val identifier = data.identifier
        val pharmacyResults = data.pharmacyResults
        
        return defaultPage("Worktype ${worktype.code} - ${worktype.name}") {
            id = "content-node"
            div("container") {
                div("row") {
                    div("col-12") {
                        h1(classes = "mt-4 mb-3") { +"Worktype ${worktype.code} - ${worktype.name}" }
                    }
                }
                
                div("row") {
                    div("col-12") {
                        div("card mb-4") {
                            div("card-header bg-success text-white") {
                                h3(classes = "card-title mb-0") { +"Worktype Details" }
                            }
                            div("card-body") {
                                div("table-responsive") {
                                    table(classes = "table table-striped table-bordered table-hover") {
                                        tbody {
                                            tr { 
                                                td { +"Code" }
                                                td { +worktype.code } 
                                            }
                                            tr { 
                                                td { +"Name" }
                                                td { +worktype.name } 
                                            }
                                            worktype.group?.let {
                                                tr { 
                                                    td { +"Group" }
                                                    td { 
                                                        a(href = localLinks(it.uri)) {
                                                            +"${it.code} - ${it.name}" 
                                                        } 
                                                    } 
                                                }
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
                                h3(classes = "card-title mb-0") { +"Experiments (${pharmacyResults.size} results)" }
                            }
                            div("card-body p-0") {
                                presentPharmacyResults(pharmacyResults, sourceType = "worktype")
                            }
                        }
                    }
                }
            }
        }
    }
}