package net.nprod.nap.pages.home

import kotlinx.html.*
import net.nprod.nap.pages.TourStep
import net.nprod.nap.pages.defaultPage
import net.nprod.nap.pages.tourStep
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

/**
 * View for the home page
 */
object HomeView {

    /**
     * One of the ways into the data, presented as a card
     *
     * Every card offers the same two things, in the same order: the way in, so a visitor
     * who has a name in mind can type it without hunting for the search box in the
     * navigation bar, and one worked example for a visitor who has nothing in mind yet.
     *
     * @param title What the visitor wants to do
     * @param body How to do it
     * @param actionLabel Label of the button that opens the search or the editor
     * @param actionHref Where that button goes
     * @param exampleHref A page or a pre-run search showing what comes out of it
     */
    private data class Entry(
        val title: String,
        val body: String,
        val actionLabel: String,
        val actionHref: String,
        val exampleHref: String
    )

    /** A short query for the editor example, kept free of the "=" the editor cannot read back */
    private val exampleQuery = """
        PREFIX n: <https://nap.nprod.net/>
        SELECT ?compound ?name
        WHERE {
            ?compound a n:compound;
                      n:name ?name.
        }
        LIMIT 10
    """.trimIndent()

    private val entries = listOf(
        Entry(
            "Start from a plant or an animal",
            "Search for a taxon by name. You get one result per name, and following it lists every experiment " +
                "recorded on that taxon.",
            "Search taxa",
            "/organism/search",
            "/organism/search?query=salix"
        ),
        Entry(
            "Start from a compound",
            "Search for a compound by name. Its page gives you its class, its codes, its synonyms and where it " +
                "shows up.",
            "Search compounds",
            "/compound/search",
            "/compound/1"
        ),
        Entry(
            "Start from an activity",
            "Read through every activity name we record, narrowing the list as you type. Following one gives you " +
                "the experiments that measured it, whatever the organism or the compound involved.",
            "Browse activities",
            "/pharmacology",
            "/pharmacology/1"
        ),
        Entry(
            "Ask something we did not anticipate",
            "The whole database is a graph you can query directly. The editor comes with examples, and results " +
                "export to CSV, JSON or XML.",
            "Open the query editor",
            "/sparql",
            "/sparql?q=" + URLEncoder.encode(exampleQuery, StandardCharsets.UTF_8).replace("+", "%20")
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
                        h1(classes = "mt-4 mb-3") { +"NAP" }
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
                                "This is NAP",
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
                                    +"Pick one of the entry points below to run your own search, or use the search "
                                    +"box at the top of any page."
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
                                    div("d-flex flex-wrap gap-2") {
                                        a(classes = "btn btn-sm btn-primary", href = entry.actionHref) {
                                            +entry.actionLabel
                                        }
                                        a(classes = "btn btn-sm btn-outline-secondary", href = entry.exampleHref) {
                                            +"See an example"
                                        }
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
