package net.nprod.nap.rdf

/**
 * Queries about taxa, the normalized name layer over the organism records
 */

/**
 * Get the name of a taxon
 *
 * A taxon is reached through the organism records that point at it, but those records
 * carry the names their publications used and any one of them can differ from the
 * reconciled name. Only the taxon itself can say what it is called.
 *
 * @param sparqlConnector The SPARQL connector
 * @param taxonId The numeric identifier of the taxon
 * @return The name of the taxon, or null when there is no such taxon or it has no name
 */
fun taxonName(sparqlConnector: SparqlConnector, taxonId: String): String? {
    val query = """
        PREFIX n: <https://nap.nprod.net/>
        SELECT ?name
        WHERE {
            <https://nap.nprod.net/taxon/$taxonId> n:name ?name.
        }
        LIMIT 1
    """.trimIndent()

    val results = sparqlConnector.getResultsOfQuery(query) ?: return null
    if (!results.hasNext()) return null

    return results.nextSolution().getLiteral("name")?.string?.takeIf { it.isNotBlank() }
}
