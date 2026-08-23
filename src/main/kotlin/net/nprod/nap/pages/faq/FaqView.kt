package net.nprod.nap.pages.faq

import kotlinx.html.*
import net.nprod.nap.pages.defaultPage

/**
 * View for the FAQ page
 *
 * Static content explaining the data model and the different parts of the project.
 */
object FaqView {

    /**
     * One entry of the entity reference table
     *
     * @param name The name used for the entity in the interface and in the URIs
     * @param path An example page for that entity
     * @param description What the entity holds
     */
    private data class EntityDescription(val name: String, val path: String, val description: String)

    private val entities = listOf(
        EntityDescription(
            "Citation", "/citation/1",
            "The publication a record was extracted from: authors, title, journal, year, DOI when we have it. " +
                "Everything in Nap ultimately hangs off a citation."
        ),
        EntityDescription(
            "Organism", "/organism/1",
            "The material that was actually studied in a publication, as it was recorded: name given by the authors, " +
                "collected part, condition of that part, country or geographical area of collection."
        ),
        EntityDescription(
            "Taxon", "/taxon/1",
            "A normalized name in a taxonomic hierarchy, with its level (class, family, genus, species, subspecies) " +
                "and a link to its parent. Many organism records point to the same taxon."
        ),
        EntityDescription(
            "Compound", "/compound/1",
            "A chemical entity, with its compound class, its publication name and code, its synonyms and its " +
                "compound code."
        ),
        EntityDescription(
            "Pharmacy", "/pharmacy/1",
            "One experiment or observation. This is the central record: it ties together an organism, a compound or " +
                "an extract, the type of work done, the activity measured, the animal model, the administration " +
                "route, the dose and the result."
        ),
        EntityDescription(
            "Pharmacology", "/pharmacology/1",
            "The activity, target or assay endpoint a pharmacy record measured."
        ),
        EntityDescription(
            "Worktype", "/worktype/1",
            "The kind of work that was done, for example a chemical isolation or a biological assay. Worktypes are " +
                "organized in groups."
        ),
        EntityDescription(
            "Extract", "/extract/1",
            "The preparation that was tested, for example a specific solvent extract, rather than a pure compound."
        ),
        EntityDescription(
            "Animal", "/animal/1",
            "The test organism or system used in the experiment."
        ),
        EntityDescription(
            "Administration route", "/administrationroute/1",
            "How the material was administered in the experiment."
        ),
        EntityDescription(
            "Country and geographical area", "/country/1",
            "Where the studied material came from."
        ),
        EntityDescription(
            "Collected part and part condition", "/partcondition/1",
            "Which part of the organism was used, and in what state it was when used."
        ),
        EntityDescription(
            "Compound code and compound synonym", "/compoundcode/1",
            "Identifiers and alternative names attached to a compound."
        )
    )

    /**
     * Render the FAQ page
     *
     * @param data The view data for the FAQ page
     * @return HTML string of the FAQ page
     */
    fun render(data: FaqViewData): String {
        return defaultPage(data.title) {
            div("container") {
                div("row") {
                    div("col-12") {
                        h1(classes = "mt-4 mb-4") { +"Frequently asked questions" }
                    }
                }

                div("row") {
                    div("col-12") {
                        section("What is Nap?") {
                            p {
                                +"Nap is an open database of ethnomedical and ethnopharmacological information. It "
                                +"collects, from the published literature, which organisms and which compounds were "
                                +"tested, how they were tested and what came out of it."
                            }
                            p {
                                +"The data comes from a subset of the Napralert database. It has been converted to RDF and "
                                +"is served from a SPARQL endpoint, which is why the pages you see are mostly views "
                                +"over graph data and why some records still carry the quirks of the original data "
                                +"entry system."
                            }
                        }

                        section("What is the difference between an organism and a taxon?") {
                            p {
                                +"An "
                                strong { +"organism" }
                                +" is a specimen record. It describes the material a given publication worked with: "
                                +"the name the authors used, the part that was collected, the condition of that part, "
                                +"where it was collected and which citation it belongs to. It is a record about one "
                                +"batch of material, not about a species."
                            }
                            p {
                                +"A "
                                strong { +"taxon" }
                                +" is a normalized name in a taxonomic hierarchy. It has a taxonomic level (class, "
                                +"family, genus, species, subspecies) and a link to its parent taxon. Taxa were "
                                +"added later to reconcile the names that had been typed record by record."
                            }
                            p {
                                +"Many organism records point to the same taxon. So if you want everything that is "
                                +"known about a species, you want the taxon; if you want to know what exactly was "
                                +"tested in one paper, you want the organism."
                            }
                            p {
                                +"Two practical consequences. First, an organism can be a mixture of several "
                                +"organisms, because that is what some publications tested. Second, some organisms "
                                +"are empty: the original entry system put the organism at the center of every "
                                +"record, so a placeholder was created even when there was no organism to describe."
                            }
                        }

                        section("Why does the search box say Taxon and not Organism?") {
                            p {
                                +"Because that is what the name search returns. It looks for the text you typed in "
                                +"the organism records, then collapses the matches to one row per taxon, so you get "
                                +"one line per name instead of one line per specimen."
                            }
                            p {
                                +"Clicking a result takes you to all the experiments recorded for that taxon, not to "
                                +"a single specimen page. Individual organism pages are still reachable from the "
                                +"experiments and from the citations they belong to."
                            }
                        }

                        section("What is a pharmacy record?") {
                            p {
                                +"It is one experiment. The name is inherited from the original database and it is a "
                                +"bit misleading: a pharmacy record is the link between a biological source, a "
                                +"tested material, a type of work, a measured activity and a result. Most of the "
                                +"other entity types exist to describe one field of a pharmacy record."
                            }
                        }

                        section("What are the different entities?") {
                            div("table-responsive") {
                                table(classes = "table table-striped table-hover") {
                                    thead {
                                        tr {
                                            th { +"Entity" }
                                            th { +"What it holds" }
                                        }
                                    }
                                    tbody {
                                        entities.forEach { entity ->
                                            tr {
                                                td { a(href = entity.path) { +entity.name } }
                                                td { +entity.description }
                                            }
                                        }
                                    }
                                }
                            }
                            p(classes = "text-muted") {
                                +"The links point at record number 1 of each type, as an example."
                            }
                        }

                        section("How do I get to the data itself?") {
                            p {
                                +"Every entity page has a JSON version at the same address: request it with an "
                                code { +"Accept: application/json" }
                                +" header."
                            }
                            p {
                                +"For anything the pages do not cover, use the "
                                a(href = "/sparql") { +"SPARQL editor" }
                                +". It runs against the same endpoint the site uses, and results can be exported as "
                                +"CSV, JSON or XML. The endpoint itself is proxied at "
                                code { +"/raw/sparql" }
                                +" if you want to query it from a script."
                            }
                            p {
                                +"Pages also show the SPARQL queries that were used to build them, at the bottom, so "
                                +"you can copy one and start from there."
                            }
                        }

                        section("Why do some pages show a raw list of properties?") {
                            p {
                                +"Not every entity type has a dedicated page yet. When there is none, the address "
                                +"falls back to a generic view that lists the incoming and outgoing links of that "
                                +"record. It is less readable, but nothing is hidden from you."
                            }
                        }

                        section("The name or the classification looks wrong. Why?") {
                            p {
                                +"Names were entered per record, over a long period, from the publications "
                                +"themselves. They carry the spelling and the taxonomy of their time, including "
                                +"typos and synonyms that have since been revised. The taxon layer reconciles what "
                                +"can be reconciled automatically, and the organism record keeps the original text "
                                +"so nothing is lost."
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Render one FAQ question with its answer
     *
     * @param question The question, used as the section heading
     * @param answer The body of the answer
     */
    private fun DIV.section(question: String, answer: DIV.() -> Unit) {
        div("card mb-4") {
            div("card-header bg-success text-white") {
                h2(classes = "card-title h4 mb-0") { +question }
            }
            div("card-body", block = answer)
        }
    }
}