package net.nprod.nap.pages.extract

import kotlinx.html.*
import net.nprod.nap.pages.defaultPage
import net.nprod.nap.pages.presentPharmacyResults

/**
 * View for rendering extract page
 */
object ExtractView {
    /**
     * Renders the extract page
     * 
     * @param data The extract data to render
     * @return HTML string representation of the page
     */
    fun render(data: ExtractViewData): String {
        val extract = data.extract
        val pharmacyResults = data.pharmacyResults
        
        return defaultPage("${extract.name} (${extract.code})") {
            id = "content-node"
            div("container-fluid container-full px-4") {
                div("row") {
                    div("col-12") {
                        h1(classes = "mt-4 mb-4") { +"Extract: ${extract.name} (${extract.code})" }
                    }
                }
                
                div("row") {
                    div("col-12") {
                        div("card mb-4") {
                            div("card-header bg-primary text-white") {
                                h3(classes = "card-title mb-0") { +"Experiments (${pharmacyResults.size} results)" }
                            }
                            div("card-body") {
                                presentPharmacyResults(pharmacyResults, sourceType = "extract")
                            }
                        }
                    }
                }
            }
        }
    }
}