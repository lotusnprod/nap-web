package net.nprod.nap.types

import net.nprod.nap.rdf.SparqlConnector

data class DoseUnit (
    val uri: String,
    val name: String
) {
    object Cache {
        private val doseunits: MutableMap<String, DoseUnit> = mutableMapOf()

        operator fun get(doseunitUri: String?): DoseUnit? {
            if (doseunitUri == null) return null

            return doseunits[doseunitUri]
        }

        init {
            val sparqlConnector = SparqlConnector()


            val query = """
           PREFIX n: <https://nap.nprod.net/>
           SELECT ?doseunit ?code ?name {
                ?doseunit a n:doseunit;
                             n:name ?name.
            }
        """.trimIndent()

            val result = sparqlConnector.getResultsOfQuery(query)
            if (result != null) {
                while (result.hasNext()) {
                    val solution = result.nextSolution()
                    val doseunitUri = solution["doseunit"].asResource().uri
                    val name = solution["name"].asLiteral().string
                    doseunits[doseunitUri] = DoseUnit(uri = doseunitUri, name = name)
                }
            }
        }
    }
}
