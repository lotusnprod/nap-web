package net.nprod.nap.pages

import kotlinx.html.BODY
import kotlinx.html.a
import kotlinx.html.div
import kotlinx.html.footer
import kotlinx.html.id
import kotlinx.html.p

/**
 * Site wide disclaimer, rendered at the bottom of every page.
 *
 * Kept deliberately understated: readable, but not competing with the content above it.
 */
fun BODY.disclaimer() {
    footer(classes = "site-disclaimer border-top mt-5 py-3") {
        id = "site-disclaimer"
        div(classes = "container") {
            p(classes = "text-muted small mb-0") {
                +"Nap is an index of what scientific publications report. It is not medical advice, and nothing "
                +"here is a recommendation to use any organism, extract or compound to prevent, diagnose or treat "
                +"any condition. Records reproduce what their source stated and may be incomplete, outdated or "
                +"wrong; we make no warranty as to their accuracy. Always go back to the cited publication before "
                +"relying on anything you find here."
            }
            p(classes = "text-muted small mb-0 mt-2") {
                a(classes = "link-secondary", href = "/faq") { +"More about the data and how it is organized" }
            }
        }
    }
}
