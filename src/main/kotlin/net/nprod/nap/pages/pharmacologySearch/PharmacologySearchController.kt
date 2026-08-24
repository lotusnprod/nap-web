package net.nprod.nap.pages.pharmacologySearch

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import net.nprod.nap.rdf.SparqlConnector
import net.nprod.nap.rdf.pharmacologySearchQuery
import net.nprod.nap.types.Pharmacology
import org.apache.jena.query.QuerySolution

/**
 * Controller for the pharmacology search page
 * Handles requests to search for pharmacology entries, and to list them all
 */
class PharmacologySearchController {
    private val sparqlConnector = SparqlConnector()

    /**
     * Handle GET request to /pharmacology and /pharmacology/search
     *
     * Without a query the page lists every activity: the names are a controlled vocabulary
     * nobody knows by heart, and searching for one supposes you can already spell it.
     *
     * @param call The application call
     */
    suspend fun handleRequest(call: ApplicationCall) {
        val queryParam = call.request.queryParameters["query"]
        val pharmacologyEntries = if (queryParam.isNullOrBlank()) {
            allActivities()
        } else {
            search(queryParam)
        }

        val data = PharmacologySearchViewData(queryParam, pharmacologyEntries)
        val html = PharmacologySearchView.render(data)
        call.respondText(html, ContentType.Text.Html)
    }

    /**
     * Every activity, by name
     *
     * Both the names and their experiment counts are cached reference data, so listing the
     * whole vocabulary costs no query of its own.
     */
    private fun allActivities(): List<Map<String, String>> {
        val counts = experimentCounts()
        return Pharmacology.Cache.values()
            .sortedWith(compareBy(String.CASE_INSENSITIVE_ORDER) { it.name })
            .map { entry(it.uri, it.name, counts) }
    }

    /**
     * The activities whose name matches the query, through the text index
     */
    private fun search(query: String): List<Map<String, String>> {
        val results = sparqlConnector.getResultsOfQuery(pharmacologySearchQuery(query), logQuery = false)
        val counts = experimentCounts()
        val entries = mutableListOf<Map<String, String>>()

        while (results != null && results.hasNext()) {
            val solution: QuerySolution = results.nextSolution()
            entries.add(
                entry(
                    uri = solution.getResource("pharmacology").uri,
                    name = solution.getLiteral("name").string,
                    counts = counts
                )
            )
        }

        return entries
    }

    private fun entry(uri: String, name: String, counts: Map<String, Int>) = mapOf(
        "uri" to uri,
        "name" to name,
        // Absent from the map means nothing was ever recorded for that activity
        "experiments" to (counts[uri] ?: 0).toString()
    )

    /**
     * How many experiments measured each activity, or nothing at all when that cannot be
     * worked out: a count missing from a column is a smaller loss than a page that fails.
     */
    private fun experimentCounts(): Map<String, Int> =
        runCatching { Pharmacology.ExperimentCounts.asMap() }.getOrDefault(emptyMap())

    companion object {
        /**
         * Register routes for the pharmacology search controller
         *
         * @param routing The routing configuration to add routes to
         */
        fun registerRoutes(routing: Routing) {
            val controller = PharmacologySearchController()
            routing.get("/pharmacology/search") {
                controller.handleRequest(call)
            }
            // The listing is what the menu points at, and /pharmacology/search reads as a
            // page that needs a term before it shows anything.
            routing.get("/pharmacology") {
                controller.handleRequest(call)
            }
        }
    }
}
