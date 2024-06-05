package net.nprod.nap.pages


import getRef
import kotlinx.html.*
import net.nprod.nap.types.Pharmacy

fun DIV.presentPharmacyResults(pharmacyResults: List<Pharmacy>, sourceType: String) {
    div(classes = "container mt-5") {
        id = "pharmacy"
        table(classes = "table table-striped table-bordered") {
            thead(classes = "thead-dark") {
                tr {
                    td { +"Experiment" }
                    td { +"Worktypes" }
                    td { +"Pharmacology" }
                    if (sourceType == "compound") {
                        td { +"Organism (specimen)" }
                    } else {
                        td { +"Compound" }
                    }
                }
            }
            tbody {
                pharmacyResults.forEach { pharmacy ->
                    tr {
                        td { a(href = pharmacy.uri) { +pharmacy.uri.getRef() } }
                        td {
                            ul {
                                pharmacy.worktypes.forEach { worktype ->
                                    li { a(href = worktype.uri) { +worktype.name } }
                                }
                            }
                        }
                        td {
                            pharmacy.pharmacology?.let { a(href = it.uri) { +it.name } }
                        }
                        if (sourceType == "compound") {
                            td {
                                pharmacy.organism?.let { a(href = it.uri) { +it.nameForHumans() } }
                            }
                        } else {
                            td {
                                ul {
                                    pharmacy.compounds.forEach { compound ->
                                        li { a(href = compound.uri) { +(compound.name ?: "Unknown compound") } }
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