package net.nprod.nap.pages

import kotlinx.html.*
import net.nprod.nap.rdf.SparqlConnector
import net.nprod.nap.pages.defaultPage
import org.apache.jena.query.ResultSet

/** The hint under the search box: what a search term is allowed to be */
private const val SEARCH_HINT =
    "Part of a name is enough. Several words are matched as one phrase, so " +
        "\"alepposide a\" finds that compound and not its siblings."

/** The hint under the filter box of a listing: the whole list is already on the page */
private const val FILTER_HINT =
    "The list narrows as you type. Several words all have to appear, in any order."

/**
 * A search box for one entity type, submitting to the route the page is served from
 *
 * A plain GET form: the navigation bar needs JavaScript to pick a route from its dropdown,
 * this one already knows where it goes.
 *
 * @param entityType The type of entity being searched, used in the placeholder
 * @param searchPath The route to submit to. Nothing is rendered when it is null.
 * @param query The current search term, kept in the field so it can be edited
 * @param inputId Identifier for the field, so client side code can find it
 * @param placeholder What the field says when empty
 * @param submitLabel What the button says. On a listing it narrows what is already there,
 *   whether or not the browser runs the script that does it without a round trip.
 * @param hint The line under the box explaining what can be typed
 */
private fun DIV.searchBox(
    entityType: String,
    searchPath: String?,
    query: String?,
    inputId: String? = null,
    placeholder: String = "${entityType.lowercase()} name",
    submitLabel: String = "Search",
    hint: String = SEARCH_HINT
) {
    if (searchPath == null) return

    form(classes = "row g-2 mb-3", method = FormMethod.get, action = searchPath) {
        attributes["role"] = "search"

        div("col-auto flex-grow-1") {
            input(classes = "form-control") {
                if (inputId != null) id = inputId
                type = InputType.search
                name = "query"
                value = query ?: ""
                this.placeholder = placeholder
                attributes["aria-label"] = "$entityType name"
            }
        }
        div("col-auto") {
            button(classes = "btn btn-primary", type = ButtonType.submit) { +submitLabel }
        }
        div("col-12") {
            span("text-muted small") { +hint }
        }
    }
}

/**
 * The table of results, shared by a search and a listing
 *
 * @param tableId Identifier for the table, so client side code can find its rows
 * @param filterTextOf What a row is matched against when the list is filtered in the browser.
 *   Without it the whole text of the row is used, which also matches the other columns.
 */
private fun DIV.resultsTable(
    entities: List<Map<String, String>>,
    renderTableHeaders: TR.() -> Unit,
    renderTableRow: TR.(Map<String, String>) -> Unit,
    tableId: String? = null,
    filterTextOf: ((Map<String, String>) -> String)? = null
) {
    div("table-responsive") {
        table(classes = "table table-striped table-hover") {
            if (tableId != null) id = tableId
            thead {
                tr {
                    renderTableHeaders()
                }
            }
            tbody {
                entities.forEach { entity ->
                    tr {
                        if (filterTextOf != null) attributes["data-filter"] = filterTextOf(entity)
                        renderTableRow(entity)
                    }
                }
            }
        }
    }
}

/**
 * Everything of one type, listed on a single page and filtered in the browser
 *
 * Searching supposes the visitor already knows what to type. A list does not: it is the only
 * way to find out what the vocabulary of a field actually is. It is worth having whenever the
 * whole set fits on a page, which is why this is opt-in rather than the default.
 *
 * @param title What the page is called, in the visitor's words rather than the schema's
 * @param intro One line saying what is listed and what following an entry gives
 * @param filterTextOf The part of an entry the filter matches on, the name by default
 */
data class SearchListing(
    val title: String,
    val intro: String? = null,
    val filterTextOf: ((Map<String, String>) -> String)? = null
)

/**
 * Generic search page template that can be used for different entity types
 *
 * @param entityType The type of entity being searched (used for display purposes)
 * @param query The search query string
 * @param searchQueryFunction A function that generates the SPARQL query for the search (can be null if preProcessedResults is provided)
 * @param processResults A function that processes the search results into a list of maps (can be null if preProcessedResults is provided)
 * @param renderTableHeaders A function that renders the table headers in the result table
 * @param renderTableRow A function that renders a row in the result table for a single entity
 * @param headerColor The color of the card header (default: "bg-success")
 * @param additionalInfoBlock Optional function to render additional info about the results
 * @param preProcessedResults Optional pre-processed results to use instead of querying the database
 * @param searchPath The route this search is served from. When given, the page carries its own
 *   search box: the one in the navigation bar is easy to miss, and a page called "Search" that
 *   offers no way to search is a dead end.
 * @param listing How to present the results when there is no query. Given one, and results to
 *   show, an empty query lists everything instead of asking for a search term.
 * @return HTML page with search results
 */
fun genericSearchPage(
    entityType: String,
    query: String?,
    searchQueryFunction: ((String) -> String)?,
    processResults: ((ResultSet) -> List<Map<String, String>>)?,
    renderTableHeaders: TR.() -> Unit,
    renderTableRow: TR.(Map<String, String>) -> Unit,
    headerColor: String = "bg-success",
    additionalInfoBlock: (DIV.(List<Map<String, String>>) -> Unit)? = null,
    preProcessedResults: List<Map<String, String>>? = null,
    searchPath: String? = null,
    listing: SearchListing? = null
): String {
    if (query.isNullOrBlank()) {
        // Nothing to list: the page still has to offer a way in.
        if (listing == null || preProcessedResults.isNullOrEmpty()) {
            return defaultPage("$entityType Search") {
                div("container") {
                    div("row") {
                        div("col-12") {
                            h1(classes = "mt-4 mb-4") { +"$entityType Search" }
                            p { +"Please enter a search term." }
                            searchBox(entityType, searchPath, query)
                        }
                    }
                }
            }
        }

        return listingPage(entityType, headerColor, preProcessedResults, renderTableHeaders, renderTableRow, searchPath, listing)
    }

    // Use pre-processed results if provided, otherwise query the database
    val entities = preProcessedResults ?: if (searchQueryFunction != null && processResults != null) {
        val sparqlConnector = SparqlConnector()

        // Get SPARQL query for entity search
        val searchQuery = searchQueryFunction(query)

        val results = sparqlConnector.getResultsOfQuery(searchQuery)
        if (results != null) processResults(results) else emptyList()
    } else {
        emptyList()
    }

    return defaultPage("$entityType Search: $query") {
        div("container") {
            div("row") {
                div("col-12") {
                    h1(classes = "mt-4 mb-4") { +"$entityType Search: $query" }
                    searchBox(entityType, searchPath, query)
                    p { +"Found ${entities.size} results." }
                    if (additionalInfoBlock != null) {
                        additionalInfoBlock(this, entities)
                    }
                }
            }

            div("row") {
                div("col-12") {
                    div("card mb-4") {
                        div("card-header $headerColor text-white") {
                            h3(classes = "card-title mb-0") { +"Search Results" }
                        }
                        div("card-body p-0") {
                            if (entities.isEmpty()) {
                                div("p-3") {
                                    p { +"No ${entityType.lowercase()} found matching '$query'." }
                                }
                            } else {
                                resultsTable(entities, renderTableHeaders, renderTableRow)
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * The whole set on one page, narrowed in the browser as the visitor types
 *
 * The form still submits without JavaScript, and lands on the server side search for the
 * same term: the filtering is an improvement on the listing, not a condition for using it.
 */
private fun listingPage(
    entityType: String,
    headerColor: String,
    entities: List<Map<String, String>>,
    renderTableHeaders: TR.() -> Unit,
    renderTableRow: TR.(Map<String, String>) -> Unit,
    searchPath: String?,
    listing: SearchListing
): String = defaultPage(listing.title) {
    div("container") {
        div("row") {
            div("col-12") {
                h1(classes = "mt-4 mb-3") { +listing.title }
                if (listing.intro != null) {
                    p(classes = "lead") { +listing.intro }
                }
                searchBox(
                    entityType = entityType,
                    searchPath = searchPath,
                    query = null,
                    inputId = "live-filter-input",
                    placeholder = "filter by name",
                    submitLabel = "Filter",
                    hint = FILTER_HINT
                )
            }
        }

        div("row") {
            div("col-12") {
                div("card mb-4") {
                    div("card-header $headerColor text-white") {
                        h3(classes = "card-title mb-0") {
                            +"Showing "
                            span { id = "live-filter-count"; +entities.size.toString() }
                            +" of ${entities.size}"
                        }
                    }
                    div("card-body p-0") {
                        resultsTable(
                            entities = entities,
                            renderTableHeaders = renderTableHeaders,
                            renderTableRow = renderTableRow,
                            tableId = "live-filter-table",
                            filterTextOf = listing.filterTextOf
                        )
                        div("p-3 d-none") {
                            id = "live-filter-empty"
                            p(classes = "mb-0") { +"No ${entityType.lowercase()} matches what you typed." }
                        }
                    }
                }
            }
        }

        script(src = "/assets/js/live-filter.js") {
            defer = true
        }
    }
}
