package net.nprod.nap.pages

import kotlinx.html.BODY
import kotlinx.html.ButtonType
import kotlinx.html.a
import kotlinx.html.button
import kotlinx.html.div
import kotlinx.html.id
import kotlinx.html.li
import kotlinx.html.nav
import kotlinx.html.span
import kotlinx.html.ul

fun BODY.navbar() {
    nav(classes = "navbar navbar-expand-lg bg-body-tertiary") {
        div(classes = "container-fluid") {
            a(classes = "navbar-brand", href = "#") { +"Nap" }

            button(
                type = ButtonType.button,
                classes = "navbar-toggler"
            ) {
                attributes["data-bs-toggle"] = "collapse"
                attributes["data-bs-target"] = "#navbarSupportedContent"
                attributes["aria-controls"] = "navbarSupportedContent"
                attributes["aria-expanded"] = "false"
                attributes["aria-label"] = "Toggle navigation"
                span(classes = "navbar-toggler-icon") {}
            }

            div(classes = "collapse navbar-collapse") {
                id = "navbarSupportedContent"

                ul(classes = "navbar-nav me-auto mb-2 mb-lg-0") {
                    li(classes = "nav-item") {
                        a(classes = "nav-link active", href = "/") {
                            attributes["aria-current"] = "page"
                            +"Home"
                        }
                    }
                    li(classes = "nav-item") {
                        a(classes = "nav-link", href = "/sparql") { +"Sparql" }
                    }
                }
            }
        }
    }
}