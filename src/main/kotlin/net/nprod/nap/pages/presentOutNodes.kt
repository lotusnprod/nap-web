package net.nprod.nap.pages

import getRef
import kotlinx.html.*
import org.apache.jena.rdf.model.RDFNode
import org.apache.jena.rdf.model.Resource
import rdfNode


fun DIV.presentTable(nodes: Map<Resource, List<RDFNode>>, _id: String, column1: String, column2: String) {
    id = _id
    table(classes = "table table-striped table-bordered table-sm") {
        thead(classes = "thead-dark") {
            tr {
                td { +column1 }
                td { +column2 }
            }
        }
        tbody {
            nodes.forEach { node ->
                if (node.value.first().toString() != "" || node.value.isNotEmpty()) {
                    tr {
                        td { +node.key.uri.getRef() }
                        td {
                            ul {
                                node.value.forEach { value ->
                                    li {
                                        rdfNode(value)
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

fun DIV.presentOutNodes(outNodes: Map<Resource, List<RDFNode>>) {
    presentTable(outNodes, "out-nodes", "Predicate", "Object")
}

fun DIV.presentInNodes(inNodes: Map<Resource, List<RDFNode>>) {
    presentTable(inNodes, "in-nodes", "Predicate", "Subject")
}
