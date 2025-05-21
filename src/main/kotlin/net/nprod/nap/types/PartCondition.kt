package net.nprod.nap.types

import kotlinx.serialization.Serializable
import net.nprod.nap.rdf.SparqlConnector

@Serializable
data class PartCondition (
    val uri: String,
    val name: String
) {

    companion object {
        fun fromSparql(sparqlConnector: SparqlConnector, uri: String): PartCondition {
            var new: PartCondition? = null

            val query = """
            PREFIX n: <https://nap.nprod.net/>
              SELECT ?partcondition ?name {
                  ?partcondition a n:partcondition;
                                n:name ?name.
                  VALUES ?partcondition { <$uri> }
            }
        """.trimIndent()

            val result = sparqlConnector.getResultsOfQuery(query)
            if (result != null) {
                while (result.hasNext()) {
                    val solution = result.nextSolution()
                    val partconditionUri = solution["partcondition"].asResource().uri
                    if (new == null) {
                        new = PartCondition(partconditionUri, solution["name"].asLiteral().string)
                    }
                }
            }
            if (new == null) throw Exception ("No partcondition found for $uri")
            return new
        }
    }

    object Cache {
        private val pharmacologies: MutableMap<String, PartCondition> = mutableMapOf()

        operator fun get(partconditionUri: String?): PartCondition? {
            if (partconditionUri == null) return null

            return pharmacologies[partconditionUri]
        }

        init {
            val sparqlConnector = SparqlConnector()


            val query = """
           PREFIX n: <https://nap.nprod.net/>
           SELECT ?partcondition ?code ?name {
                ?partcondition a n:partcondition;
                             n:name ?name.
            }
        """.trimIndent()

            val result = sparqlConnector.getResultsOfQuery(query)
            if (result != null) {
                while (result.hasNext()) {
                    val solution = result.nextSolution()
                    val partconditionUri = solution["partcondition"].asResource().uri
                    val name = solution["name"].asLiteral().string
                    pharmacologies[partconditionUri] = PartCondition(uri = partconditionUri, name = name)
                }
            }
        }
    }
}
