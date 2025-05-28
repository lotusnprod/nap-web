package net.nprod.nap.types

import net.nprod.nap.rdf.SparqlConnector
import kotlinx.serialization.Serializable

@Serializable
data class AdministrationRoute (
    val uri: String,
    val name: String
) {
    object Cache {
        private val administrationRoutes: MutableMap<String, AdministrationRoute> = mutableMapOf()

        operator fun get(administrationRouteUri: String?): AdministrationRoute? {
            if (administrationRouteUri == null) return null

            return administrationRoutes[administrationRouteUri]
        }

        init {
            val sparqlConnector = SparqlConnector()

            // Looks like we are missing a type here...
            val query = """
           PREFIX n: <https://nap.nprod.net/>
           SELECT DISTINCT ?administrationRoute ?name {
                ?x n:administeredBy ?administrationRoute .
                ?administrationRoute n:name ?name.
            }
        """.trimIndent()

            val result = sparqlConnector.getResultsOfQuery(query, logQuery = false)
            if (result != null) {
                while (result.hasNext()) {
                    val solution = result.nextSolution()
                    val administrationRouteUri = solution["administrationRoute"].asResource().uri
                    val name = solution["name"].asLiteral().string
                    administrationRoutes[administrationRouteUri] = AdministrationRoute(uri = administrationRouteUri, name = name)
                }
            }
        }
    }
}
