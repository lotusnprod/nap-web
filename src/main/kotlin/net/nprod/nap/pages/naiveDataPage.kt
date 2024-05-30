package net.nprod.nap.pages

import dataPage
import genURI
import kotlinx.html.*
import net.nprod.nap.rdf.SparqlConnector

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

        if (outNodes.isNotEmpty()) {
            h2 { +"Out nodes" }
            div { presentOutNodes(outNodes) }
        }

        if (inNodes.isNotEmpty()) {
            h2 { +"In nodes" }
            div { presentInNodes(inNodes) }
        }
    }
}

