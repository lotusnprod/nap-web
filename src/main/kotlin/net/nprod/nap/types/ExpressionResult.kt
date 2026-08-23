package net.nprod.nap.types

import kotlinx.serialization.Serializable
import net.nprod.nap.rdf.SparqlConnector

@Serializable
data class ExpressionResult (
    val uri: String,
    val code: String,
    val name: String
) {
    object Cache : ReferenceCache<ExpressionResult>() {
        override fun load(sparqlConnector: SparqlConnector): Map<String, ExpressionResult> {
            val expressionResults = mutableMapOf<String, ExpressionResult>()

            val query = """
           PREFIX n: <https://nap.nprod.net/>
           SELECT ?expressionResult ?code ?name {
                ?expressionResult a n:expressionresult;
                             n:code ?code;
                             n:name ?name.
            }
        """.trimIndent()

            val result = sparqlConnector.getResultsOfQuery(query, logQuery = false)
            if (result != null) {
                while (result.hasNext()) {
                    val solution = result.nextSolution()
                    val expressionResultUri = solution["expressionResult"].asResource().uri
                    val code = solution["code"].asLiteral().string
                    val name = solution["name"].asLiteral().string
                    expressionResults[expressionResultUri] = ExpressionResult(uri = expressionResultUri, code = code, name = name)
                }
            }
            return expressionResults
        }
    }
}
