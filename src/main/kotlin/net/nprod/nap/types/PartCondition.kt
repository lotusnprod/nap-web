package net.nprod.nap.types

import net.nprod.nap.rdf.SparqlConnector

data class Partcondition (
    val uri: String,
    val name: String
) {

    companion object {
        fun fromSparql(sparqlConnector: SparqlConnector, uri: String): Partcondition {
            var new: Partcondition? = null

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
                        new = Partcondition(partconditionUri, solution["name"].asLiteral().string)
                    }
                }
            }
            if (new == null) throw Exception ("No partcondition found for $uri")
            return new
        }
    }

    object Cache {
        private val pharmacologies: MutableMap<String, Partcondition> = mutableMapOf()

        operator fun get(partconditionUri: String?): Partcondition? {
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
                    pharmacologies[partconditionUri] = Partcondition(uri = partconditionUri, name = name)
                }
            }
        }
    }
}
