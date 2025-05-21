package net.nprod.nap.pages.pharmacology

import kotlinx.html.*
import net.nprod.nap.pages.defaultPage
import net.nprod.nap.pages.presentPharmacyResults

/**
 * View for rendering pharmacology page
 */
object PharmacologyView {
    /**
     * Renders the pharmacology page
     * 
     * @param data The pharmacology data to render
     * @return HTML string representation of the page
     */
    fun render(data: PharmacologyViewData): String {
        val pharmacology = data.pharmacology
        val pharmacyResults = data.pharmacyResults
        
        return defaultPage("${pharmacology.name} (${data.identifier})") {
            id = "content-node"
            div("container") {
                div("row") {
                    div("col-12") {
                        h1(classes = "mt-4 mb-4") { +"${pharmacology.name} (${data.identifier})" }
                    }
                }
                
                div("row") {
                    div("col-12") {
                        div("card mb-4") {
                            div("card-header bg-primary text-white") {
                                h3(classes = "card-title mb-0") { +"Experiments (${pharmacyResults.size} results)" }
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