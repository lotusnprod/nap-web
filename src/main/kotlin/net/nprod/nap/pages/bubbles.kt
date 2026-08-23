package net.nprod.nap.pages

import kotlinx.html.*
import net.nprod.nap.helpers.localLinks

/**
 * The kinds of entities that are displayed as a colored bubble, with the color used
 * for each of them. Every page must use these so that a worktype (or a compound, …)
 * looks the same wherever it is shown.
 */
enum class BubbleKind(val backgroundClass: String, val textClass: String) {
    WORKTYPE("bg-primary", "text-white"),
    COMPOUND("bg-success", "text-white"),
    PHARMACOLOGY("bg-purple", "text-white"),
    ORGANISM("bg-organism", "text-dark"),
    ALERT("bg-warning", "text-dark"),
    RESULT("bg-secondary", "text-white")
}

/**
 * Render a single entity bubble, linking to the entity when a URI is given
 *
 * @param kind The kind of entity, it defines the color of the bubble
 * @param label The text displayed in the bubble
 * @param uri The URI of the entity, when it is null the bubble is not clickable
 */
fun FlowContent.bubble(kind: BubbleKind, label: String, uri: String? = null) {
    span(classes = "badge bubble ${kind.backgroundClass} ${kind.textClass}") {
        if (uri == null) {
            +label
        } else {
            a(href = localLinks(uri), classes = "${kind.textClass} text-decoration-none") { +label }
        }
    }
}

/**
 * Render a wrapping row of bubbles
 */
fun FlowContent.bubbleList(block: DIV.() -> Unit) {
    div("d-flex flex-wrap gap-1 bubble-list", block)
}
