package net.nprod.nap.pages.invalidEntry

import kotlinx.html.div
import kotlinx.html.id
import kotlinx.html.dom.createHTMLDocument
import kotlinx.html.dom.serialize

/**
 * View for rendering invalid entry page
 */
object InvalidEntryView {
    /**
     * Renders the invalid entry page
     * 
     * @param data The invalid entry data to render
     * @return HTML string representation of the page
     */
    fun render(data: InvalidEntryViewData): String {
        val doc = createHTMLDocument().div {
            id = "test-node"
            +"Invalid id ${data.value} for ${data.type}"
        }
        return doc.serialize(true)
    }
}