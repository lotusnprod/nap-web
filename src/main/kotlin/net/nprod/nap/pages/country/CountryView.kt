package net.nprod.nap.pages.country

import kotlinx.html.*
import net.nprod.nap.helpers.localLinks
import net.nprod.nap.pages.defaultPage
import net.nprod.nap.pages.presentPharmacyResults

/**
 * View for rendering country page
 */
object CountryView {
    /**
     * Renders the country page
     * 
     * @param data The country data to render
     * @return HTML string representation of the page
     */
    fun render(data: CountryViewData): String {
        val country = data.country
        val pharmacies = data.pharmacies
        
        return defaultPage("Country: ${country.name}") {
            id = "content-node"
            div("container") {
                div("row") {
                    div("col-12") {
                        h1(classes = "mt-4 mb-4") { +"Country: ${country.name}" }
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
                                            td { +country.name }
                                        }
                                        tr {
                                            th { +"URI" }
                                            td { a(href = localLinks(country.uri)) { +country.uri } }
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
                                    h3(classes = "card-title mb-0") { +"Experiments in this country or with organisms from this country (${pharmacies.size} results)" }
                                }
                                div("card-body p-0") {
                                    presentPharmacyResults(pharmacies, sourceType = "country")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}