package net.nprod.nap.pages

import kotlinx.html.*
import net.nprod.nap.rdf.SparqlConnector
import defaultPage
import net.nprod.nap.helpers.localLinks
import net.nprod.nap.rdf.organismSearchQuery

/**
 * Search for organisms by name
 * @param query The search query
 * @return HTML page with search results
 */
fun organismSearchPage(query: String?): String {
    return genericSearchPage(
        entityType = "Organism",
        query = query,
        searchQueryFunction = ::organismSearchQuery,
        processResults = { results ->
            val organisms = mutableListOf<Map<String, String>>()
            
            while (results.hasNext()) {
                val solution = results.nextSolution()
                val organismUri = solution["organism"].asResource().uri
                val genusname = solution["genusname"]?.asLiteral()?.string ?: ""
                val speciesname = solution["speciesname"]?.asLiteral()?.string ?: ""
                val subspeciesname = solution["subspeciesname"]?.asLiteral()?.string ?: ""
                val familyname = solution["familyname"]?.asLiteral()?.string ?: ""
                val number = solution["number"]?.asLiteral()?.int?.toString() ?: "Unknown"
                val taxon = solution["taxon"]?.asResource()?.uri

                val displayName = buildString {
                    if (genusname.isNotBlank()) {
                        append(genusname.replaceFirstChar { it.uppercase() })
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

                organisms.add(mapOf(
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
            val groupedByTaxon = organisms.groupBy { it["taxon"] }
            val uniqueOrganisms = groupedByTaxon
                .filter { it.key?.isNotEmpty() == true } // Filter out organisms without taxon
                .map { it.value.first() } // Take the first organism for each taxon
            
            uniqueOrganisms
        },
        renderTableHeaders = {
            th { +"Name" }
            th { +"Family" }
            th { +"Subspecies" }
        },
        renderTableRow = { organism ->
            td { 
                val taxon = organism["taxon"]!!
                if (taxon.isNotEmpty()) {
                    val taxonId = taxon.split("/").last()
                    a(href = "/pharmacy_search?taxon_id=$taxonId") { +organism["displayName"]!! }
                } else {
                    val uri = organism["uri"]!!
                    a(href = localLinks(uri)) { +organism["displayName"]!! }
                }
            }
            td { +organism["familyname"]!! }
            td { +organism["subspeciesname"]!! }
        },
        additionalInfoBlock = { uniqueOrganisms ->
            val totalOrganisms = uniqueOrganisms.size
            p { +"Found $totalOrganisms unique taxa." }
        }
    )
}