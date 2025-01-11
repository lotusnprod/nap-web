package net.nprod.nap.types

import net.nprod.nap.rdf.SparqlConnector

data class DoseExpression (
    val uri: String,
    val name: String
) {
    object Cache {
        private val doseexpressions: MutableMap<String, DoseExpression> = mutableMapOf()

        operator fun get(doseexpressionUri: String?): DoseExpression? {
            if (doseexpressionUri == null) return null

            return doseexpressions[doseexpressionUri]
        }

        init {
            val sparqlConnector = SparqlConnector()


            val query = """
           PREFIX n: <https://nap.nprod.net/>
           SELECT ?doseexpression ?code ?name {
                ?doseexpression a n:doseexpression;
                             n:name ?name.
            }
        """.trimIndent()

            val result = sparqlConnector.getResultsOfQuery(query)
            if (result != null) {
                while (result.hasNext()) {
                    val solution = result.nextSolution()
                    val doseexpressionUri = solution["doseexpression"].asResource().uri
                    val name = solution["name"].asLiteral().string
                    doseexpressions[doseexpressionUri] = DoseExpression(uri = doseexpressionUri, name = name)
                }
            }
        }
    }
}
