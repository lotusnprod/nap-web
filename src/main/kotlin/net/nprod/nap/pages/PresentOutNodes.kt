package net.nprod.nap.pages

import getRef
import kotlinx.html.*
import org.apache.jena.rdf.model.RDFNode
import org.apache.jena.rdf.model.Resource
import rdfNode

fun DIV.presentOutNodes(outNodes: Map<Resource, List<RDFNode>>) {
    id = "out-nodes"
    outNodes.forEach { (key, values) ->
        val filtered_values = values.filter { it.toString() != "" }
        if (filtered_values.isNotEmpty()) {
            h3 { +"Predicatu: ${key.uri.getRef()}" }
            ul {
                filtered_values.forEach {
                    li {
                        classes = setOf("predicate-object")
                        rdfNode(it)
                    }
                }
            }
        }
    }
}