package net.nprod.nap.types

import kotlinx.serialization.Serializable
import net.nprod.nap.rdf.SparqlConnector

@Serializable
data class CompoundCode(
    var uri: String,
    var name: String
) {
    companion object {
        /**
         * Retrieves a CompoundCode from the SPARQL endpoint
         *
         * @param uri The URI of the CompoundCode to retrieve
         * @param sparqlConnector The SPARQL connector
         * @return The CompoundCode object or null if not found
         */
        fun fromSparql(uri: String, sparqlConnector: SparqlConnector): CompoundCode? {
            val sparql = """
                PREFIX n: <https://nap.nprod.net/>
                SELECT ?name
                WHERE {
                    <$uri> n:name ?name.
                }
            """.trimIndent()
            
            val result = sparqlConnector.getResultsOfQuery(sparql)
            if (result == null || !result.hasNext()) return null
            
            val solution = result.nextSolution()
            val name = solution.getLiteral("name")?.string ?: return null
            return CompoundCode(uri, name)
        }
    }
}