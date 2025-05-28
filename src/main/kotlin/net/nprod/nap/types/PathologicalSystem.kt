package net.nprod.nap.types

import kotlinx.serialization.Serializable
import net.nprod.nap.rdf.SparqlConnector

@Serializable
data class PathologicalSystem (
    val uri: String,
    val code: String,
    val name: String
) {
    object Cache {
        private val pathologicalSystems: MutableMap<String, PathologicalSystem> = mutableMapOf()

        operator fun get(pathologicalSystemUri: String?): PathologicalSystem? {
            if (pathologicalSystemUri == null) return null

            return pathologicalSystems[pathologicalSystemUri]
        }

        init {
            val sparqlConnector = SparqlConnector()


            val query = """
           PREFIX n: <https://nap.nprod.net/>
           SELECT ?pathologicalSystem ?code ?name {
                ?pathologicalSystem a n:pathologicalSystem;
                             n:code ?code;
                             n:name ?name.
            }
        """.trimIndent()

            val result = sparqlConnector.getResultsOfQuery(query, logQuery = false)
            if (result != null) {
                while (result.hasNext()) {
                    val solution = result.nextSolution()
                    val pathologicalSystemUri = solution["pathologicalSystem"].asResource().uri
                    val code = solution["code"].asLiteral().string
                    val name = solution["name"].asLiteral().string
                    pathologicalSystems[pathologicalSystemUri] = PathologicalSystem(uri = pathologicalSystemUri, code = code, name = name)
                }
            }
        }
    }
}
