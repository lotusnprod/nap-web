package net.nprod.nap.pages

import kotlinx.html.div
import kotlinx.html.dom.createHTMLDocument
import kotlinx.html.dom.serialize
import kotlinx.html.id

fun invalidEntryPage(type: String, value: String): String {
    val doc = createHTMLDocument().div {
        id = "test-node"
        +"Invalid id $value for $type"
    }
    return doc.serialize(true)
}