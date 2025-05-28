package net.nprod.nap.types

import kotlinx.serialization.Serializable
import net.nprod.nap.rdf.SparqlConnector

@Serializable
data class PerUnit (
    val uri: String,
    val name: String
) {
    object Cache {
        private val perunits: MutableMap<String, PerUnit> = mutableMapOf()

        operator fun get(perunitUri: String?): PerUnit? {
            if (perunitUri == null) return null

            return perunits[perunitUri]
        }

        init {
            val sparqlConnector = SparqlConnector()

            // Looks like we are missing a type here...
            val query = """
           PREFIX n: <https://nap.nprod.net/>
           SELECT DISTINCT ?perunit ?name {
                ?x n:has_perunit ?perunit .
                ?perunit n:name ?name.
            }
        """.trimIndent()

            val result = sparqlConnector.getResultsOfQuery(query, logQuery = false)
            if (result != null) {
                while (result.hasNext()) {
                    val solution = result.nextSolution()
                    val perunitUri = solution["perunit"].asResource().uri
                    val name = solution["name"].asLiteral().string
                    perunits[perunitUri] = PerUnit(uri = perunitUri, name = name)
                }
            }
        }
    }
}
