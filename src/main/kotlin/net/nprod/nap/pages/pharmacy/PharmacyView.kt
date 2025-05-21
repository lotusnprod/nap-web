package net.nprod.nap.pages.pharmacy

import net.nprod.nap.helpers.localLinks
import net.nprod.nap.pages.defaultPage
import kotlinx.html.*

/**
 * View for rendering pharmacy page
 */
object PharmacyView {
    /**
     * Renders the pharmacy page
     * 
     * @param data The pharmacy data to render
     * @return HTML string representation of the page
     */
    fun render(data: PharmacyViewData): String {
        val pharmacy = data.pharmacy
        
        return defaultPage("Experiment ${data.identifier}") {
            id = "content-node"
            div("container") {
                div("row") {
                    div("col-12") {
                        h1(classes = "mt-4 mb-4") { +"Experiment ${data.identifier}" }
                    }
                }

                div("row") {
                    if (pharmacy.worktypes.isNotEmpty()) {
                        div("col-md-4 mb-4") {
                            div("card") {
                                div("card-header bg-primary text-white") {
                                    h3(classes = "card-title mb-0") { +"Worktypes" }
                                }
                                div("card-body") {
                                    div("list-group") {
                                        pharmacy.worktypes.forEach { worktype ->
                                            a(
                                                href = localLinks(worktype.uri),
                                                classes = "list-group-item list-group-item-action"
                                            ) {
                                                +worktype.name
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    if (pharmacy.compounds.isNotEmpty()) {
                        div("col-md-4 mb-4") {
                            div("card") {
                                div("card-header bg-success text-white") {
                                    h3(classes = "card-title mb-0") { +"Compounds isolated" }
                                }
                                div("card-body") {
                                    div("list-group") {
                                        pharmacy.compounds.forEach { compound ->
                                            a(
                                                href = localLinks(compound.uri),
                                                classes = "list-group-item list-group-item-action"
                                            ) {
                                                +(compound.name ?: "Unknown compound")
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    if (pharmacy.alerts.isNotEmpty()) {
                        div("col-md-4 mb-4") {
                            div("card") {
                                div("card-header bg-warning text-dark") {
                                    h3(classes = "card-title mb-0") { +"Alerts" }
                                }
                                div("card-body") {
                                    div("list-group") {
                                        pharmacy.alerts.forEach { alert ->
                                            a(
                                                href = localLinks(alert.uri),
                                                classes = "list-group-item list-group-item-action"
                                            ) {
                                                +alert.name
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                div("row") {
                    div("col-12") {
                        div("card mb-4") {
                            div("card-header bg-info text-white") {
                                h3(classes = "card-title mb-0") { +"Details" }
                            }
                            div("card-body") {
                                div("table-responsive") {
                                    table(classes = "table table-striped table-bordered table-hover") {
                                        thead {
                                            tr {
                                                th { +"Property" }
                                                th { +"Value" }
                                            }
                                        }
                                        tbody {
                                            pharmacy.pharmacology?.let {
                                                tr {
                                                    td { +"Pharmacology" }
                                                    td { a(href = localLinks(it.uri)) { +it.name } }
                                                }
                                            }
                                            pharmacy.administrationRoute?.let {
                                                tr {
                                                    td { +"Administration route" }
                                                    td { a(href = localLinks(it.uri)) { +it.name } }
                                                }
                                            }
                                            pharmacy.organism?.let {
                                                tr {
                                                    td { +"Organism" }
                                                    td { a(href = localLinks(it.uri)) { +it.nameForHumans() } }
                                                }
                                            }
                                            pharmacy.organism?.citation?.let { citation ->
                                                tr {
                                                    td { +"Citation" }
                                                    td {
                                                        a(href = localLinks(citation.uri)) {
                                                            +citation.formatCitation()
                                                        }
                                                        citation.doi?.let { doi ->
                                                            if (doi.isNotEmpty()) {
                                                                br {}
                                                                a(
                                                                    href = "https://doi.org/$doi",
                                                                    target = "_blank"
                                                                ) { +"DOI: $doi" }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                            pharmacy.activitylevel?.let {
                                                tr {
                                                    td { +"Activity level" }
                                                    td { +it }
                                                }
                                            }
                                            pharmacy.amtutil.let {
                                                tr {
                                                    td { +"Amount utilized" }
                                                    td { it + " " + pharmacy.amtutilunit }
                                                }
                                            }
                                            pharmacy.cmpdiso.let {
                                                tr {
                                                    td { +"Compound isolated" }
                                                    td { it + " " + pharmacy.cmpdisounit }
                                                }
                                            }
                                            pharmacy.disease?.let {
                                                tr {
                                                    td { +"Disease" }
                                                    td { +it }
                                                }
                                            }
                                            pharmacy.doseamount?.let {
                                                tr {
                                                    td { +"Dose amount" }
                                                    td {
                                                        +it
                                                        pharmacy.doseUnit?.let {
                                                            +" "
                                                            a(href = localLinks(it.uri)) { +it.name }
                                                        }
                                                    }
                                                }
                                            }
                                            pharmacy.number?.let {
                                                tr {
                                                    td { +"Number" }
                                                    td { +it }
                                                }
                                            }
                                            pharmacy.percentyield?.let {
                                                tr {
                                                    td { +"Percent yield" }
                                                    td { +it }
                                                }
                                            }
                                            pharmacy.rawyield?.let {
                                                tr {
                                                    td { +"Raw yield" }
                                                    td { +it }
                                                }
                                            }
                                            pharmacy.pharmactivity?.let {
                                                tr {
                                                    td { +"Pharmacological activity" }
                                                    td { +it }
                                                }
                                            }
                                            pharmacy.quantitativeresult?.let {
                                                tr {
                                                    td { +"Quantitative result" }
                                                    td { +it }
                                                }
                                            }
                                            pharmacy.qualitativeResult?.let {
                                                tr {
                                                    td { +"Qualitative result" }
                                                    td { a(href = localLinks(it.uri)) { +it.name } }
                                                }
                                            }
                                            pharmacy.animal?.let {
                                                tr {
                                                    td { +"Animal" }
                                                    td { a(href = localLinks(it.uri)) { +it.name } }
                                                }
                                            }
                                            pharmacy.perUnit?.let {
                                                tr {
                                                    td { +"Per unit" }
                                                    td { a(href = localLinks(it.uri)) { +it.name } }
                                                }
                                            }
                                            pharmacy.doseExpression?.let {
                                                tr {
                                                    td { +"Dose expression" }
                                                    td { a(href = localLinks(it.uri)) { +it.name } }
                                                }
                                            }
                                            pharmacy.extract?.let {
                                                tr {
                                                    td { +"Extract" }
                                                    td { a(href = localLinks(it.uri)) { +it.name } }
                                                }
                                            }
                                            pharmacy.pathologicalSystem?.let {
                                                tr {
                                                    td { +"Pathological system" }
                                                    td { a(href = localLinks(it.uri)) { +it.name } }
                                                }
                                            }
                                            pharmacy.gender?.let {
                                                tr {
                                                    td { +"Gender" }
                                                    td { a(href = localLinks(it.uri)) { +it.name } }
                                                }
                                            }
                                            pharmacy.expressionResult?.let {
                                                tr {
                                                    td { +"Expression result" }
                                                    td { a(href = localLinks(it.uri)) { +it.name } }
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