package net.nprod.nap.pages

import as_local_link_if_dev
import kotlinx.html.*
import kotlinx.html.stream.createHTML
import net.nprod.nap.rdf.SparqlConnector
import net.nprod.nap.rdf.compoundsByCompoundCode
import net.nprod.nap.types.CompoundCode
import genURI

/**
 * Page that displays all compounds with a specific compound code
 *
 * @param id the ID of the compound code
 * @return HTML string
 */
fun compoundCodePage(id: String?): String {
    if (id == null) {
        return invalidEntryPage("compoundcode", "null")
    }

    val uri = genURI("compoundcode", id)
    val sparqlConnector = SparqlConnector()

    // Get the compound code information
    val compoundCode = CompoundCode.fromSparql(uri, sparqlConnector)
    if (compoundCode == null) {
        return invalidEntryPage("compoundcode", id)
    }

    // Get compounds with this code
    val compounds = compoundsByCompoundCode(uri, sparqlConnector)

    return createHTML().html {
        head {
            genericHead("Compound Code: ${compoundCode.name}")
        }
        body {
            navbar()
            div("container mt-4") {
                div("row") {
                    div("col-md-12") {
                        h2 { +"Compound Code: ${compoundCode.name}" }
                        p { +"Compounds with this code: ${compounds.size}" }
                    }
                }

                if (compounds.isNotEmpty()) {
                    div("row mt-4") {
                        div("col-md-12") {
                            div("card") {
                                div("card-header bg-success text-white") {
                                    h4 { +"Compounds" }
                                }
                                div("card-body") {
                                    div("list-group") {
                                        compounds.forEach { compound ->
                                            a(classes = "list-group-item list-group-item-action", 
                                              href = compound.uri.as_local_link_if_dev) {
                                                div("d-flex w-100 justify-content-between") {
                                                    h5("mb-1") { +(compound.name ?: "Unnamed Compound") }
                                                    small { +"ID: ${compound.uri.split("/").last()}" }
                                                }
                                                if (compound.synonyms.isNotEmpty()) {
                                                    p("mb-1") { 
                                                        +"Synonyms: ${compound.synonyms.joinToString(", ") { it.name }}" 
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                } else {
                    div("row mt-4") {
                        div("col-md-12") {
                            div("alert alert-warning") {
                                +"No compounds found with this code."
                            }
                        }
                    }
                }
            }
            defaultFinalScripts()
        }
    }
}