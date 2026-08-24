package net.nprod.nap.pages.home

import kotlinx.html.*
import net.nprod.nap.pages.TourStep
import net.nprod.nap.pages.defaultPage
import net.nprod.nap.pages.tourStep

/**
 * View for the home page
 */
object HomeView {

    /**
     * One of the ways into the data, presented as a card
     *
     * @param title What the visitor wants to do
     * @param body How to do it
     * @param linkLabel Label of the example link
     * @param linkHref Where the example goes
     */
    private data class Entry(val title: String, val body: String, val linkLabel: String, val linkHref: String)

    private val entries = listOf(
        Entry(
            "Start from a plant or an animal",
            "Search for a taxon by name. You get one result per name, and following it lists every experiment " +
                "recorded on that taxon.",
            "Try salix",
            "/organism/search?query=salix"
        ),
        Entry(
            "Start from a compound",
            "Search for a compound by name. Its page gives you its class, its codes, its synonyms and where it " +
                "shows up.",
            "Browse a compound",
            "/compound/1"
        ),
        Entry(
            "Start from an activity",
            "Search for a pharmacology to find the experiments that measured it, whatever the organism or the " +
                "compound involved.",
            "Browse a pharmacology",
            "/pharmacology/1"
        ),
        Entry(
            "Ask something we did not anticipate",
            "The whole database is a graph you can query directly. The editor comes with examples, and results " +
                "export to CSV, JSON or XML.",
            "Open the query editor",
            "/sparql"
        )
    )

    /**
     * Render the home page HTML
     * @param data The view data for the home page
     * @return HTML string of the home page
     */
    fun render(data: HomeViewData): String {
        return defaultPage(data.title) {
            div("container") {
                div("row") {
                    div("col-12") {
                        h1(classes = "mt-4 mb-3") { +"Nap" }
                        p(classes = "lead") {
                            +"Natural Actives & Pharmacology. An open database of ethnomedical and "
                            +"ethnopharmacological information: which organisms and which compounds were studied in "
                            +"the scientific literature, how they were studied and what was found."
                        }
                    }
                }

                div("row") {
                    div("col-12") {
                        div("card mb-4") {
                            tourStep(
                                TourStep.HOME_INTRO,
                                "This is Nap",
                                "Every record here comes from a publication. This tour shows you how to get from a " +
                                    "name to the experiments behind it. Next takes you through it, Turn off ends it."
                            )
                            div("card-header bg-success text-white") {
                                h2(classes = "card-title h4 mb-0") { +"How it works" }
                            }
                            div("card-body") {
                                p {
                                    +"Everything starts from a "
                                    strong { +"citation" }
                                    +", a published paper. From it we record the "
                                    strong { +"organism" }
                                    +" that was studied, which part of it and where it came from, and the "
                                    strong { +"experiments" }
                                    +" that were run on it: what was tested, on what model, at what dose, with what "
                                    +"result."
                                }
                                p {
                                    +"Organism records are specimens, one per publication, so the same species "
                                    +"appears many times under slightly different names. A "
                                    strong { +"taxon" }
                                    +" layer reconciles those names, which is why searching by name gives you taxa "
                                    +"and why a taxon is the shortest route to everything known about a species. "
                                    +"The "
                                    a(href = "/faq") { +"FAQ" }
                                    +" goes through this in more detail."
                                }
                                p(classes = "mb-0") {
                                    +"Use the search box at the top of any page to get started, or pick one of the "
                                    +"entry points below."
                                }
                            }
                        }
                    }
                }

                div("row") {
                    entries.forEach { entry ->
                        div("col-md-6") {
                            div("card mb-4 h-100") {
                                div("card-body") {
                                    h3(classes = "card-title h5") { +entry.title }
                                    p(classes = "card-text") { +entry.body }
                                    a(classes = "btn btn-sm btn-outline-primary", href = entry.linkHref) {
                                        +entry.linkLabel
                                    }
                                }
                            }
                        }
                    }
                }

                div("row") {
                    div("col-12") {
                        p(classes = "text-muted") {
                            +"New here? Turn on "
                            strong { +"Tutorial" }
                            +" in the menu above and the pages will walk you through what to click. You can turn it "
                            +"off again at any time from the same place."
                        }
                    }
                }
            }
        }
    }
}
