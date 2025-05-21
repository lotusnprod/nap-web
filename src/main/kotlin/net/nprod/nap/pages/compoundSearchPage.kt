package net.nprod.nap.pages

import kotlinx.html.*
import net.nprod.nap.rdf.SparqlConnector
import net.nprod.nap.pages.defaultPage
import net.nprod.nap.helpers.localLinks
import net.nprod.nap.rdf.compoundSearchQuery
import net.nprod.nap.pages.compoundSearch.CompoundSearchController

/**
 * Search for compounds by name
 * @param query The search query
 * @return HTML page with search results
 * @deprecated Use CompoundSearchController instead
 */
@Deprecated("Use CompoundSearchController instead", ReplaceWith("CompoundSearchController"))
fun compoundSearchPage(query: String?): String {
    return genericSearchPage(
        entityType = "Compound",
        query = query,
        searchQueryFunction = ::compoundSearchQuery,
        processResults = { results ->
            val compounds = mutableListOf<Map<String, String>>()
            
            while (results.hasNext()) {
                val solution = results.nextSolution()
                val compoundUri = solution["compound"].asResource().uri
                val name = solution["name"].asLiteral().string
                val compoundClass = solution["compoundClass"]?.asLiteral()?.string ?: "Unknown"
                val number = solution["number"]?.asLiteral()?.int?.toString() ?: "Unknown"

                compounds.add(mapOf(
                    "uri" to compoundUri,
                    "name" to name,
                    "compoundClass" to compoundClass,
                    "number" to number
                ))
            }
            
            compounds
        },
        renderTableHeaders = {
            th { +"Name" }
            th { +"Compound Class" }
        },
        renderTableRow = { compound ->
            td { 
                val uri = compound["uri"]!!
                a(href = localLinks(uri)) {
                    +compound["name"]!!
                }
            }
            td { +compound["compoundClass"]!! }
        }
    )
}