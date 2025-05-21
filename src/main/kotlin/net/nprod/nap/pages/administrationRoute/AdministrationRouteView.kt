package net.nprod.nap.pages.administrationRoute

import kotlinx.html.*
import net.nprod.nap.helpers.capitalize
import net.nprod.nap.pages.defaultPage
import net.nprod.nap.pages.presentPharmacyResults
import net.nprod.nap.types.AdministrationRoute
import net.nprod.nap.types.Pharmacy

/**
 * View for rendering administration route page
 */
object AdministrationRouteView {
    /**
     * Renders the administration route page
     * 
     * @param data The administration route data to render
     * @return HTML string representation of the page
     */
    fun render(data: AdministrationRouteViewData): String {
        val titleText = "${data.administrationRoute.name.capitalize()} Administration Route (${data.identifier})"
        
        return defaultPage(titleText) {
            id = "content-node"
            div("container") {
                div("row") {
                    div("col-12") {
                        h1(classes = "mt-4 mb-4") { +titleText }
                    }
                }
                
                div("row") {
                    div("col-12") {
                        div("card mb-4") {
                            div("card-header bg-primary text-white") {
                                h3(classes = "card-title mb-0") { +"Experiments (${data.pharmacyResults.size} results)" }
                            }
                            div("card-body p-0") {
                                presentPharmacyResults(data.pharmacyResults, sourceType = "administrationroute")
                            }
                        }
                    }
                }
            }
        }
    }
}