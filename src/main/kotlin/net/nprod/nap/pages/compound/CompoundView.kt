package net.nprod.nap.pages.compound

import kotlinx.html.*
import net.nprod.nap.helpers.localLinks
import net.nprod.nap.pages.defaultPage
import net.nprod.nap.pages.presentPharmacyResults

/**
 * View for rendering compound page
 */
object CompoundView {
    /**
     * Renders the compound page
     * 
     * @param data The compound data to render
     * @return HTML string representation of the page
     */
    fun render(data: CompoundViewData): String {
        val compound = data.compound
        val identifier = data.identifier
        val pharmacyResults = data.pharmacies

        return defaultPage("${compound.name} ($identifier)") {
            id = "content-node"
            div("container") {
                div("row") {
                    div("col-12") {
                        h1(classes = "mt-4 mb-4") { +"${compound.name} ($identifier)" }
                    }
                }

                div("row") {
                    div("col-8") {
                        div("card mb-4") {
                            div("card-header bg-success text-white") {
                                h3(classes = "card-title mb-0") { +"Compound Details" }
                            }
                            div("card-body") {
                                div("table-responsive") {
                                    table(classes = "table table-striped table-hover") {
                                        tbody {
                                            compound.name?.let {
                                                tr {
                                                    th(scope = ThScope.row, classes = "w-25") { +"Name" }
                                                    td { +it }
                                                }
                                            }
                                            compound.compoundClass?.let {
                                                tr {
                                                    th(scope = ThScope.row) { +"Compound class" }
                                                    td { +it }
                                                }
                                            }
                                            compound.publicationCode?.let {
                                                tr {
                                                    th(scope = ThScope.row) { +"Publication code" }
                                                    td { +it }
                                                }
                                            }
                                            compound.publicationName?.let {
                                                tr {
                                                    th(scope = ThScope.row) { +"Publication name" }
                                                    td { +it }
                                                }
                                            }
                                            compound.compoundCode?.let {
                                                tr {
                                                    th(scope = ThScope.row) { +"Compound code" }
                                                    td { a(href = localLinks(it.uri)) { +it.name } }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        if (compound.synonyms.isNotEmpty()) {
                            div("card mb-4") {
                                div("card-header bg-info text-white") {
                                    h3(classes = "card-title mb-0") { +"Synonyms" }
                                }
                                div("card-body p-0") {
                                    div("list-group list-group-flush") {
                                        compound.synonyms.forEach { synonym ->
                                            a(
                                                href = localLinks(synonym.uri),
                                                classes = "list-group-item list-group-item-action d-flex justify-content-between align-items-center"
                                            ) {
                                                +synonym.name
                                                span("badge bg-primary rounded-pill") { +"view" }
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
                                p { +"Compound class: ${compound.compoundClass ?: "Unknown"}" }
                                p { +"Experiments: ${pharmacyResults.size}" }
                                hr {}
                                div("d-grid gap-2") {
                                    a(href = "#experiments", classes = "btn btn-primary") {
                                        +"View Experiments"
                                    }
                                }
                            }
                        }
                    }
                }

                div("row") {
                    div("col-12") {
                        div("card mb-4") {
                            id = "experiments"
                            div("card-header bg-primary text-white") {
                                h3(classes = "card-title mb-0") {
                                    +"Experiments (${pharmacyResults.size})"
                                }
                            }
                            div("card-body p-0") {
                                presentPharmacyResults(pharmacyResults, sourceType = "compound")
                            }
                        }
                    }
                }
            }
        }
    }
}