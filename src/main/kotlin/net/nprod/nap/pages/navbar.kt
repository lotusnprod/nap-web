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
            a(classes = "navbar-brand", href = "#") { +"NAP" }

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
                    // The activity names are a vocabulary of their own: worth being able to
                    // read through without having to guess a term to search for first.
                    li(classes = "nav-item") {
                        a(classes = "nav-link", href = "/pharmacology") {
                            +"Activities"
                        }
                    }
                    li(classes = "nav-item") {
                        a(classes = "nav-link", href = "/sparql") {
                            tourStep(
                                TourStep.SPARQL,
                                "Ask your own question",
                                "The pages only show what we thought of showing. This is the query editor: it runs " +
                                    "SPARQL against the same data, and exports the results as CSV, JSON or XML.",
                                shared = true
                            )
                            +"Sparql"
                        }
                    }
                    li(classes = "nav-item") {
                        a(classes = "nav-link", href = "/faq") {
                            tourStep(
                                TourStep.FAQ,
                                "The rest is here",
                                "What the records mean, how organisms and taxa differ, and how to get the data out. " +
                                    "That is the end of the tour.",
                                shared = true
                            )
                            +"FAQ"
                        }
                    }
                    tutorialToggle()
                }

                form(classes = "d-flex") {
                    attributes["role"] = "search"
                    id = "search-form"

                    select(classes = "form-select me-2") {
                        id = "search-type"
                        style = "width: auto;"
                        tourStep(
                            TourStep.SEARCH_TYPE,
                            "Pick what you are looking for",
                            "A taxon if you start from a plant or an animal, a compound if you start from a " +
                                "chemical, a pharmacology if you start from an activity.",
                            shared = true
                        )

                        option {
                            value = "compound"
                            +"Compound" 
                        }
                        // The organism name search collapses its results to one row per taxon,
                        // so it is presented as a taxon search. See /faq for the distinction.
                        option {
                            value = "taxon"
                            +"Taxon"
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
                        tourStep(
                            TourStep.SEARCH_INPUT,
                            "Type a name",
                            "Part of a name is enough, for example \"salix\" or \"quercetin\". The search is on " +
                                "names as they were recorded, so try a shorter fragment if you get nothing.",
                            shared = true
                        )
                    }

                    button(classes = "btn btn-outline-success", type = ButtonType.submit) {
                        tourStep(
                            TourStep.SEARCH_SUBMIT,
                            "Then search",
                            "You get a list of matches. Following one takes you to the experiments recorded for it.",
                            shared = true
                        )
                        +"Search"
                    }
                }
            }
        }
    }
}
