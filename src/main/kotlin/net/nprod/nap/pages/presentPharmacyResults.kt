package net.nprod.nap.pages


import as_local_link_if_dev
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
                    td { +"Organism (specimen)" }
                    if (sourceType != "compound") {
                        td { +"Compound" }
                    }
                }
            }
            tbody {
                pharmacyResults.forEach { pharmacy ->
                    tr {
                        td { a(href = pharmacy.uri.as_local_link_if_dev.as_local_link_if_dev) { +pharmacy.uri.as_local_link_if_dev.getRef() } }
                        td {
                            ul {
                                pharmacy.worktypes.forEach { worktype ->
                                    li { a(href = worktype.uri.as_local_link_if_dev) { +worktype.name } }
                                }
                            }
                        }
                        td {
                            pharmacy.pharmacology?.let { a(href = it.uri.as_local_link_if_dev) { +it.name } }
                        }
                        td {
                            pharmacy.organism?.let { a(href = it.uri.as_local_link_if_dev) { +it.nameForHumans() } }
                        }

                        if (sourceType !== "compound") {
                            td {
                                ul {
                                    pharmacy.compounds.forEach { compound ->
                                        li { a(href = compound.uri.as_local_link_if_dev) { +(compound.name ?: "Unknown compound") } }
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