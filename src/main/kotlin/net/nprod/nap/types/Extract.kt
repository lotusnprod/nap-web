package net.nprod.nap.types

import kotlinx.serialization.Serializable
import net.nprod.nap.rdf.SparqlConnector

@Serializable
data class Extract (
    val uri: String,
    val code: String,
    val name: String
) {
    object Cache {
        private val extracts: MutableMap<String, Extract> = mutableMapOf()

        operator fun get(extractUri: String?): Extract? {
            if (extractUri == null) return null

            return extracts[extractUri]
        }

        init {
            val sparqlConnector = SparqlConnector()


            val query = """
           PREFIX n: <https://nap.nprod.net/>
           SELECT ?extract ?code ?name {
                ?extract a n:extract;
                             n:code ?code;
                             n:name ?name.
            }
        """.trimIndent()

            val result = sparqlConnector.getResultsOfQuery(query, logQuery = false)
            if (result != null) {
                while (result.hasNext()) {
                    val solution = result.nextSolution()
                    val extractUri = solution["extract"].asResource().uri
                    val code = solution["code"].asLiteral().string
                    val name = solution["name"].asLiteral().string
                    extracts[extractUri] = Extract(uri = extractUri, code = code, name = name)
                }
            }
        }
    }
}
