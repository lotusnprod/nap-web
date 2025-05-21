package net.nprod.nap.pages.compoundSynonym

import kotlinx.html.*
import net.nprod.nap.helpers.localLinks
import net.nprod.nap.pages.defaultPage

/**
 * View for rendering compound synonym page
 */
object CompoundSynonymView {
    /**
     * Renders the compound synonym page
     * 
     * @param data The compound synonym data to render
     * @return HTML string representation of the page
     */
    fun render(data: CompoundSynonymViewData): String {
        val synonym = data.synonym
        val compounds = data.compounds
        val identifier = data.identifier

        return defaultPage("Compound Synonym: ${synonym.name} ($identifier)") {
            id = "content-node"
            div("container") {
                div("row") {
                    div("col-12") {
                        h1(classes = "mt-4 mb-4") { +"Compound Synonym: ${synonym.name} ($identifier)" }
                    }
                }
                
                div("row") {
                    div("col-8") {
                        div("card mb-4") {
                            div("card-header bg-success text-white") {
                                h3(classes = "card-title mb-0") { +"Synonym Details" }
                            }
                            div("card-body") {
                                div("table-responsive") {
                                    table(classes = "table table-striped table-hover") {
                                        tbody {
                                            tr { 
                                                th(scope = ThScope.row, classes = "w-25") { +"Name" }
                                                td { +synonym.name } 
                                            }
                                            tr { 
                                                th(scope = ThScope.row) { +"URI" }
                                                td { +synonym.uri } 
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    
                    div("col-4") {
                        div("card mb-4") {
                            div("card-header bg-secondary text-white") {
                                h3(classes = "card-title mb-0") { +"Summary" }
                            }
                            div("card-body") {
                                p { +"Associated Compounds: ${compounds.size}" }
                            }
                        }
                    }
                }
                
                div("row") {
                    div("col-12") {
                        div("card mb-4") {
                            id = "compounds"
                            div("card-header bg-primary text-white") {
                                h3(classes = "card-title mb-0") { 
                                    +"Associated Compounds (${compounds.size})" 
                                }
                            }
                            div("card-body p-0") {
                                if (compounds.isEmpty()) {
                                    div("p-3") {
                                        +"No compounds found associated with this synonym."
                                    }
                                } else {
                                    div("list-group list-group-flush") {
                                        compounds.forEach { compound ->
                                            a(href = localLinks(compound.uri), 
                                              classes = "list-group-item list-group-item-action d-flex justify-content-between align-items-center") {
                                                div {
                                                    span("fw-bold") { +(compound.name ?: "Unnamed") }
                                                    compound.compoundClass?.let { compoundClass ->
                                                        span("ms-2 badge bg-secondary") { +compoundClass }
                                                    }
                                                }
                                                span("badge bg-primary rounded-pill") { +"view" }
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
}