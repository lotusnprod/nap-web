package net.nprod.nap.types

import net.nprod.nap.rdf.SparqlConnector

data class ExpressionResult (
    val uri: String,
    val code: String,
    val name: String
) {
    object Cache {
        private val expressionResults: MutableMap<String, ExpressionResult> = mutableMapOf()

        operator fun get(expressionResultUri: String?): ExpressionResult? {
            if (expressionResultUri == null) return null

            return expressionResults[expressionResultUri]
        }

        init {
            val sparqlConnector = SparqlConnector()


            val query = """
           PREFIX n: <https://nap.nprod.net/>
           SELECT ?expressionResult ?code ?name {
                ?expressionResult a n:expressionresult;
                             n:code ?code;
                             n:name ?name.
            }
        """.trimIndent()

            val result = sparqlConnector.getResultsOfQuery(query)
            if (result != null) {
                while (result.hasNext()) {
                    val solution = result.nextSolution()
                    val expressionResultUri = solution["expressionResult"].asResource().uri
                    val code = solution["code"].asLiteral().string
                    val name = solution["name"].asLiteral().string
                    expressionResults[expressionResultUri] = ExpressionResult(uri = expressionResultUri, code = code, name = name)
                }
            }
        }
    }
}
