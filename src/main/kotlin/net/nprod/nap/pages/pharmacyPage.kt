package net.nprod.nap.pages

import as_local_link_if_dev
import dataPage
import genURI
import kotlinx.html.*
import net.nprod.nap.rdf.SparqlConnector
import net.nprod.nap.types.Organism
import net.nprod.nap.types.Pharmacy

fun pharmacyPage(identifier: String?): String {
    if (identifier?.toIntOrNull() == null)
        return invalidEntryPage("pharmacy", identifier ?: "null")

    // Generate URI
    val uri = genURI("pharmacy", identifier)

    val sparqlConnector = SparqlConnector()


    val pharmacy = Pharmacy.fromSparql(sparqlConnector, uri)

    return dataPage("Experiment $identifier") {
        id = "content-node"
        h1 { +"Experiment $identifier" }

        h2 { +"Worktypes" }
        ul {
            pharmacy.worktypes.forEach { worktype ->
                li { a(href = worktype.uri.as_local_link_if_dev) { +worktype.name } }
            }
        }

        h2 { +"Compounds isolated" }
        ul {
            pharmacy.compounds.forEach { compound ->
                li { a(href = compound.uri.as_local_link_if_dev) { +(compound.name ?: "Unknown compound") } }
            }
        }

        h2 { +"Alerts" }
        ul {
            pharmacy.alerts.forEach { alert ->
                li { a(href = alert.uri.as_local_link_if_dev) { +alert.name } }
            }
        }

        h2 { +"Details" }

        table {
            tbody {
                pharmacy.pharmacology?.let {
                    tr {
                        td { +"Pharmacology" }
                        td { a(href = it.uri.as_local_link_if_dev) { +it.name } }
                    }
                }
                pharmacy.administrationRoute?.let {
                    tr {
                        td { +"Administration route" }
                        td { a(href = it.uri.as_local_link_if_dev) { +it.name } }
                    }
                }
                pharmacy.organism?.let {
                    tr {
                        td { +"Organism" }
                        td { a(href = it.uri.as_local_link_if_dev) { +it.nameForHumans() } }
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
                        td { +it
                            pharmacy.doseUnit?.let {
                                a(href = it.uri.as_local_link_if_dev) { +it.name }
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
                        td { a(href = it.uri.as_local_link_if_dev) { +it.name } }
                    }
                }
                pharmacy.animal?.let {
                    tr {
                        td { +"Animal" }
                        td { a(href = it.uri.as_local_link_if_dev) { +it.name } }
                    }
                }
                pharmacy.perUnit?.let {
                    tr {
                        td { +"Per unit" }
                        td { a(href = it.uri.as_local_link_if_dev) { +it.name } }
                    }
                }

                pharmacy.doseExpression?.let {
                    tr {
                        td { +"Dose expression" }
                        td { a(href = it.uri.as_local_link_if_dev) { +it.name } }
                    }
                }
                pharmacy.extract?.let {
                    tr {
                        td { +"Extract" }
                        td { a(href = it.uri.as_local_link_if_dev) { +it.name } }
                    }
                }
                pharmacy.pathologicalSystem?.let {
                    tr {
                        td { +"Pathological system" }
                        td { a(href = it.uri.as_local_link_if_dev) { +it.name } }
                    }
                }
                pharmacy.gender?.let {
                    tr {
                        td { +"Gender" }
                        td { a(href = it.uri.as_local_link_if_dev) { +it.name } }
                    }
                }
                pharmacy.expressionResult?.let {
                    tr {
                        td { +"Expression result" }
                        td { a(href = it.uri.as_local_link_if_dev) { +it.name } }
                    }
                }
            }
        }
    }
}

