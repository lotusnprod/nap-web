package net.nprod.nap.pages

import kotlinx.html.*
import net.nprod.nap.rdf.SparqlConnector
import defaultPage
import net.nprod.nap.helpers.localLinks
import net.nprod.nap.rdf.pharmacologySearchQuery

/**
 * Search for pharmacology entries by name
 * @param query The search query
 * @return HTML page with search results
 */
fun pharmacologySearchPage(query: String?): String {
    return genericSearchPage(
        entityType = "Pharmacology",
        query = query,
        searchQueryFunction = ::pharmacologySearchQuery,
        processResults = { results ->
            val pharmacologyEntries = mutableListOf<Map<String, String>>()
            
            while (results.hasNext()) {
                val solution = results.nextSolution()
                val pharmacologyUri = solution["pharmacology"].asResource().uri
                val name = solution["name"].asLiteral().string

                pharmacologyEntries.add(mapOf(
                    "uri" to pharmacologyUri,
                    "name" to name
                ))
            }
            
            pharmacologyEntries
        },
        renderTableHeaders = {
            th { +"Name" }
        },
        renderTableRow = { entry ->
            td { 
                val uri = entry["uri"]!!
                a(href = localLinks(uri)) { +entry["name"]!! }
            }
        },
        headerColor = "bg-primary"
    )
}