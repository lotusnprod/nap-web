package net.nprod.nap.types

import kotlinx.serialization.Serializable
import net.nprod.nap.rdf.SparqlConnector

@Serializable
data class DoseExpression (
    val uri: String,
    val name: String
) {
    object Cache : ReferenceCache<DoseExpression>() {
        override fun load(sparqlConnector: SparqlConnector): Map<String, DoseExpression> {
            val doseexpressions = mutableMapOf<String, DoseExpression>()

            val query = """
           PREFIX n: <https://nap.nprod.net/>
           SELECT ?doseexpression ?code ?name {
                ?doseexpression a n:doseexpression;
                             n:name ?name.
            }
        """.trimIndent()

            val result = sparqlConnector.getResultsOfQuery(query, logQuery = false)
            if (result != null) {
                while (result.hasNext()) {
                    val solution = result.nextSolution()
                    val doseexpressionUri = solution["doseexpression"].asResource().uri
                    val name = solution["name"].asLiteral().string
                    doseexpressions[doseexpressionUri] = DoseExpression(uri = doseexpressionUri, name = name)
                }
            }
            return doseexpressions
        }
    }
}
