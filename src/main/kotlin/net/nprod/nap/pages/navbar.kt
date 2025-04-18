package net.nprod.nap.pages

import kotlinx.html.BODY
import kotlinx.html.ButtonType
import kotlinx.html.a
import kotlinx.html.button
import kotlinx.html.div
import kotlinx.html.form
import kotlinx.html.id
import kotlinx.html.input
import kotlinx.html.li
import kotlinx.html.nav
import kotlinx.html.option
import kotlinx.html.select
import kotlinx.html.span
import kotlinx.html.style
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

                form(classes = "d-flex") {
                    attributes["role"] = "search"
                    id = "search-form"

                    select(classes = "form-select me-2") {
                        id = "search-type"
                        style = "width: auto;"

                        option { 
                            value = "compound"
                            +"Compound" 
                        }
                        option { 
                            value = "organism"
                            +"Organism" 
                        }
                        option { 
                            value = "pharmacology"
                            +"Pharmacology" 
                        }
                    }

                    input(classes = "form-control me-2") {
                        type = kotlinx.html.InputType.search
                        placeholder = "Search"
                        attributes["aria-label"] = "Search"
                        id = "search-input"
                        name = "search-query"
                    }

                    button(classes = "btn btn-outline-success", type = ButtonType.submit) {
                        +"Search"
                    }
                }
            }
        }
    }
}
