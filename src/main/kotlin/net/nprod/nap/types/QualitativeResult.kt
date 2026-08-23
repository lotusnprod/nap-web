package net.nprod.nap.types

import kotlinx.serialization.Serializable
import net.nprod.nap.rdf.SparqlConnector

@Serializable
data class QualitativeResult (
    val uri: String,
    val name: String
) {
    object Cache : ReferenceCache<QualitativeResult>() {
        override fun load(sparqlConnector: SparqlConnector): Map<String, QualitativeResult> {
            val qualitativeResults = mutableMapOf<String, QualitativeResult>()

            val query = """
           PREFIX n: <https://nap.nprod.net/>
           SELECT DISTINCT ?qualitativeResult ?name {
                ?x n:has_qualitativeresult ?qualitativeResult .
                ?qualitativeResult n:name ?name.
            }
        """.trimIndent()

            val result = sparqlConnector.getResultsOfQuery(query, logQuery = false)
            if (result != null) {
                while (result.hasNext()) {
                    val solution = result.nextSolution()
                    val qualitativeResultUri = solution["qualitativeResult"].asResource().uri
                    val name = solution["name"].asLiteral().string
                    qualitativeResults[qualitativeResultUri] = QualitativeResult(uri = qualitativeResultUri, name = name)
                }
            }
            return qualitativeResults
        }
    }
}
