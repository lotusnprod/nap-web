package net.nprod.nap.pages

import dataPage
import genURI
import getRef
import kotlinx.html.*
import net.nprod.nap.rdf.SparqlConnector
import rdfNode

fun naiveDataPage(type: String, identifier: String?): String {
    if (identifier == null || identifier.toIntOrNull() == null)
        return invalidEntryPage(type, identifier ?: "null")

    if (type.contains("/"))
        return invalidEntryPage("type", type)

    // Generate URI
    val uri = genURI(type, identifier)

    val sparqlConnector = SparqlConnector()

    // Nodes going out
    val outNodes = sparqlConnector.predicateAndObjectsOf(uri)

    // Nodes going in
    val inNodes = sparqlConnector.subjectAndPredicatesOf(uri)

    val capitalized = type.replaceFirstChar(Char::titlecase)
    return dataPage("$capitalized page for $identifier") {
        id = "content-node"
        h1 { +"$capitalized page for $identifier" }
        h2 { +"Out nodes" }
        div { presentOutNodes(outNodes) }

        h2 { +"In nodes" }

        div {
            id = "out-nodes"
            inNodes.keys.forEach {
                val entries = inNodes[it]
                h3 { +"Predicate: ${it.uri.getRef()}" }
                ul {
                    entries?.forEach {
                        li {
                            classes = setOf("subject-predicate")
                            rdfNode(it)
                        }
                    }
                }

            }
        }
    }
}

