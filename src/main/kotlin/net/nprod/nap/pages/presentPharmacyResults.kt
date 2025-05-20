package net.nprod.nap.pages


import net.nprod.nap.helpers.localLinks
import getRef
import kotlinx.html.*
import net.nprod.nap.types.Pharmacy

fun DIV.presentPharmacyResults(pharmacyResults: List<Pharmacy>, sourceType: String) {
    if (pharmacyResults.isEmpty()) {
        div(classes = "alert alert-info mt-3") {
            +"No experiments found."
        }
        return
    }
    
    div(classes = "table-responsive") {
        id = "pharmacy"
        table(classes = "table table-striped table-bordered table-hover") {
            thead {
                tr(classes = "bg-light") {
                    th { +"Experiment" }
                    th { +"Worktypes" }
                    th { +"Pharmacology" }
                    th { +"Organism (specimen)" }
                    if (sourceType != "compound") {
                        th { +"Compound" }
                    }
                }
            }
            tbody {
                pharmacyResults.forEach { pharmacy ->
                    tr {
                        td { 
                            a(href = localLinks(pharmacy.uri), classes = "font-weight-bold") { 
                                +localLinks(pharmacy.uri).getRef() 
                            } 
                        }
                        td {
                            if (pharmacy.worktypes.isNotEmpty()) {
                                div("d-flex flex-wrap gap-1") {
                                    pharmacy.worktypes.forEach { worktype ->
                                        span(classes = "badge bg-primary me-1 mb-1") { 
                                            a(href = localLinks(worktype.uri), classes = "text-white text-decoration-none") { 
                                                +worktype.name 
                                            } 
                                        }
                                    }
                                }
                            }
                        }
                        td {
                            pharmacy.pharmacology?.let { 
                                a(href = localLinks(it.uri)) { +it.name } 
                            }
                        }
                        td {
                            pharmacy.organism?.let { 
                                a(href = localLinks(it.uri)) { +it.nameForHumans() } 
                            }
                        }

                        if (sourceType !== "compound") {
                            td {
                                if (pharmacy.compounds.isNotEmpty()) {
                                    div("d-flex flex-wrap gap-1") {
                                        pharmacy.compounds.forEach { compound ->
                                            span(classes = "badge bg-success me-1 mb-1") { 
                                                a(href = localLinks(compound.uri), classes = "text-white text-decoration-none") { 
                                                    +(compound.name ?: "Unknown compound") 
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
}