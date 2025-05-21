package net.nprod.nap.pages.naiveData

import kotlinx.html.*
import net.nprod.nap.helpers.localLinks
import net.nprod.nap.pages.defaultPage
import getRef

/**
 * View for rendering naive data page
 */
object NaiveDataView {
    /**
     * Renders the naive data page
     * 
     * @param data The naive data to render
     * @return HTML string representation of the page
     */
    fun render(data: NaiveDataViewData): String {
        val type = data.type
        val identifier = data.identifier
        val outNodes = data.outNodes
        val inNodes = data.inNodes
        
        val capitalized = type.replaceFirstChar(Char::titlecase)
        
        return defaultPage("$capitalized page for $identifier") {
            id = "content-node"
            h1 { +"$capitalized page for $identifier" }

            if (outNodes.isNotEmpty()) {
                h2 { +"Out nodes" }
                div { 
                    id = "out-nodes"
                    table(classes = "table table-striped table-bordered table-sm") {
                        thead(classes = "thead-dark") {
                            tr {
                                td { +"Predicate" }
                                td { +"Object" }
                            }
                        }
                        tbody {
                            outNodes.forEach { (predicate, values) ->
                                if (values.isNotEmpty() && values.first().isNotEmpty()) {
                                    tr {
                                        td { +predicate.getRef() }
                                        td {
                                            ul {
                                                values.forEach { value ->
                                                    li {
                                                        // Check if it's a URI (simple check)
                                                        if (value.startsWith("http")) {
                                                            a(href = localLinks(value)) { +localLinks(value) }
                                                        } else {
                                                            +value
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

            if (inNodes.isNotEmpty()) {
                h2 { +"In nodes" }
                div {
                    id = "in-nodes"
                    table(classes = "table table-striped table-bordered table-sm") {
                        thead(classes = "thead-dark") {
                            tr {
                                td { +"Predicate" }
                                td { +"Subject" }
                            }
                        }
                        tbody {
                            inNodes.forEach { (predicate, values) ->
                                if (values.isNotEmpty() && values.first().isNotEmpty()) {
                                    tr {
                                        td { +predicate.getRef() }
                                        td {
                                            ul {
                                                values.forEach { value ->
                                                    li {
                                                        // Check if it's a URI (simple check)
                                                        if (value.startsWith("http")) {
                                                            a(href = localLinks(value)) { +localLinks(value) }
                                                        } else {
                                                            +value
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
    }
}