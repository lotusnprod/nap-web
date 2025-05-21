package net.nprod.nap.pages.compoundCode

import kotlinx.html.*
import net.nprod.nap.helpers.localLinks
import net.nprod.nap.pages.defaultPage

/**
 * View for rendering compound code page
 */
object CompoundCodeView {
    /**
     * Renders the compound code page
     * 
     * @param data The compound code data to render
     * @return HTML string representation of the page
     */
    fun render(data: CompoundCodeViewData): String {
        val compoundCode = data.compoundCode
        val compounds = data.compounds

        return defaultPage("Compound Code: ${compoundCode.name}") {
            id = "content-node"
            div("container mt-4") {
                div("row") {
                    div("col-md-12") {
                        h2 { +"Compound Code: ${compoundCode.name}" }
                        p { +"Compounds with this code: ${compounds.size}" }
                    }
                }

                if (compounds.isNotEmpty()) {
                    div("row mt-4") {
                        div("col-md-12") {
                            div("card") {
                                div("card-header bg-success text-white") {
                                    h4 { +"Compounds" }
                                }
                                div("card-body") {
                                    div("list-group") {
                                        compounds.forEach { compound ->
                                            a(classes = "list-group-item list-group-item-action", 
                                              href = localLinks(compound.uri)) {
                                                div("d-flex w-100 justify-content-between") {
                                                    h5("mb-1") { +(compound.name ?: "Unnamed Compound") }
                                                    small { +"ID: ${compound.uri.split("/").last()}" }
                                                }
                                                if (compound.synonyms.isNotEmpty()) {
                                                    p("mb-1") { 
                                                        +"Synonyms: ${compound.synonyms.joinToString(", ") { it.name }}" 
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                } else {
                    div("row mt-4") {
                        div("col-md-12") {
                            div("alert alert-warning") {
                                +"No compounds found with this code."
                            }
                        }
                    }
                }
            }
        }
    }
}