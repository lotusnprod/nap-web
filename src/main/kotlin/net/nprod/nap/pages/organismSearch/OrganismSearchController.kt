package net.nprod.nap.pages.organismSearch

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import net.nprod.nap.rdf.SparqlConnector
import net.nprod.nap.rdf.organismSearchQuery
import org.apache.jena.query.QuerySolution

/**
 * Controller for the organism search page
 * Handles requests to search for organisms
 */
class OrganismSearchController {
    private val sparqlConnector = SparqlConnector()
    
    /**
     * Handle GET request to /organism/search
     * Searches for organisms matching the query parameter
     *
     * @param call The application call
     */
    suspend fun handleRequest(call: ApplicationCall) {
        val queryParam = call.request.queryParameters["query"]
        val organisms = if (!queryParam.isNullOrBlank()) {
            val results = sparqlConnector.getResultsOfQuery(organismSearchQuery(queryParam), logQuery = false)
            val organismList = mutableListOf<Map<String, String>>()
            
            while (results != null && results.hasNext()) {
                val solution: QuerySolution = results.nextSolution()
                val organismUri = solution.getResource("organism").uri
                val genusname = solution.getLiteral("genusname")?.string ?: ""
                val speciesname = solution.getLiteral("speciesname")?.string ?: ""
                val subspeciesname = solution.getLiteral("subspeciesname")?.string ?: ""
                val familyname = solution.getLiteral("familyname")?.string ?: ""
                val number = solution.getLiteral("number")?.int?.toString() ?: "Unknown"
                val taxon = solution.getResource("taxon")?.uri

                val displayName = buildString {
                    if (genusname.isNotBlank()) {
                        append(genusname.lowercase().replaceFirstChar { it.uppercase() })
                        if (speciesname.isNotBlank()) {
                            append(" ")
                            append(speciesname.lowercase())
                            if (subspeciesname.isNotBlank()) {
                                append(" subsp. ")
                                append(subspeciesname.lowercase())
                            }
                        }
                        if (familyname.isNotBlank()) {
                            append(" (")
                            append(familyname)
                            append(")")
                        }
                    } else {
                        append("Organism #$number")
                    }
                }

                organismList.add(mapOf(
                    "uri" to organismUri,
                    "displayName" to displayName,
                    "genusname" to genusname,
                    "speciesname" to speciesname,
                    "subspeciesname" to subspeciesname,
                    "familyname" to familyname,
                    "number" to number,
                    "taxon" to (taxon ?: "")
                ))
            }
            
            // Group organisms by taxon and only show the first one per taxon
            val groupedByTaxon = organismList.groupBy { it["taxon"] }
            val uniqueOrganisms = groupedByTaxon
                .filter { it.key?.isNotEmpty() == true } // Filter out organisms without taxon
                .map { it.value.first() } // Take the first organism for each taxon
            
            uniqueOrganisms
        } else {
            emptyList()
        }

        val data = OrganismSearchViewData(queryParam, organisms)
        val html = OrganismSearchView.render(data)
        call.respondText(html, ContentType.Text.Html)
    }

    companion object {
        /**
         * Register routes for the organism search controller
         *
         * @param routing The routing configuration to add routes to
         */
        fun registerRoutes(routing: Routing) {
            val controller = OrganismSearchController()
            routing.get("/organism/search") {
                controller.handleRequest(call)
            }
        }
    }
}