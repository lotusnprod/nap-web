package net.nprod.nap.types

import kotlinx.serialization.Serializable
import net.nprod.nap.rdf.SparqlConnector

@Serializable
data class Alert (
    val uri: String,
    val name: String
) {
    object Cache {
        private val alerts: MutableMap<String, Alert> = mutableMapOf()

        operator fun get(alertUri: String?): Alert? {
            if (alertUri == null) return null

            return alerts[alertUri]
        }

        init {
            val sparqlConnector = SparqlConnector()


            val query = """
           PREFIX n: <https://nap.nprod.net/>
           SELECT ?alert ?name {
                ?alert a n:a3;
                       n:name ?name.
            }
        """.trimIndent()

            val result = sparqlConnector.getResultsOfQuery(query, logQuery = false)
            if (result != null) {
                while (result.hasNext()) {
                    val solution = result.nextSolution()
                    val alertUri = solution["alert"].asResource().uri
                    val name = solution["name"].asLiteral().string
                    alerts[alertUri] = Alert(uri = alertUri, name = name)
                }
            }
        }
    }
}
