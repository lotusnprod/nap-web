package net.nprod.nap.types

import kotlinx.serialization.Serializable
import net.nprod.nap.rdf.SparqlConnector

@Serializable
data class PathologicalSystem (
    val uri: String,
    val code: String,
    val name: String
) {
    object Cache : ReferenceCache<PathologicalSystem>() {
        override fun load(sparqlConnector: SparqlConnector): Map<String, PathologicalSystem> {
            val pathologicalSystems = mutableMapOf<String, PathologicalSystem>()

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
            return pathologicalSystems
        }
    }
}
