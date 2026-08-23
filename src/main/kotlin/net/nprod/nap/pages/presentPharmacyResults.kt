package net.nprod.nap.pages


import net.nprod.nap.helpers.localLinks
import getRef
import kotlinx.html.*
import net.nprod.nap.types.Pharmacy

/**
 * Render a list of experiments as a table, together with the faceted filter panel
 * that narrows it down client side (see [pharmacyFacets]).
 *
 * @param pharmacyResults The experiments to display
 * @param sourceType The kind of entity the experiments are displayed for, the column
 *                   holding that entity is dropped from the table
 */
fun DIV.presentPharmacyResults(pharmacyResults: List<Pharmacy>, sourceType: String) {
    if (pharmacyResults.isEmpty()) {
        div(classes = "alert alert-info mt-3") {
            +"No experiments found."
        }
        return
    }

    div("facet-layout") {
        pharmacyFacets(pharmacyResults)

        div("facet-results") {
            div("d-flex justify-content-between align-items-center mb-2") {
                span(classes = "text-muted small") {
                    id = "pharmacy-count"
                    +"${pharmacyResults.size} experiments"
                }
            }

            div(classes = "alert alert-info d-none") {
                id = "pharmacy-no-results"
                +"No experiment matches the selected filters."
            }

            pharmacyResultsTable(pharmacyResults, sourceType)
        }
    }

    pharmacyFacetsScript()
}

private fun DIV.pharmacyResultsTable(pharmacyResults: List<Pharmacy>, sourceType: String) {
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
                    tr(classes = "pharmacy-row") {
                        attributes["data-worktype"] =
                            pharmacy.worktypes.joinToString("|") { it.uri.getRef() }
                        attributes["data-pharmacology"] =
                            pharmacy.pharmacology?.uri?.getRef() ?: ""
                        attributes["data-compound"] =
                            pharmacy.compounds.joinToString("|") { it.uri.getRef() }
                        attributes["data-organism"] =
                            pharmacy.organism?.uri?.getRef() ?: ""
                        td {
                            a(href = localLinks(pharmacy.uri), classes = "font-weight-bold") {
                                +localLinks(pharmacy.uri).getRef()
                            }
                        }
                        td {
                            if (pharmacy.worktypes.isNotEmpty()) {
                                bubbleList {
                                    pharmacy.worktypes.forEach { worktype ->
                                        bubble(BubbleKind.WORKTYPE, worktype.name, worktype.uri)
                                    }
                                }
                            }
                        }
                        td {
                            pharmacy.pharmacology?.let { pharmacology ->
                                bubbleList {
                                    bubble(BubbleKind.PHARMACOLOGY, pharmacology.name, pharmacology.uri)
                                }
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
                                    bubbleList {
                                        pharmacy.compounds.forEach { compound ->
                                            bubble(
                                                BubbleKind.COMPOUND,
                                                compound.name ?: "Unknown compound",
                                                compound.uri
                                            )
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
