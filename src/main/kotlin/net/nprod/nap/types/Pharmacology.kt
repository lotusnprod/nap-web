package net.nprod.nap.types

import net.nprod.nap.rdf.SparqlConnector

data class Pharmacology (
    val uri: String,
    val name: String
) {
    object Cache {
        private val pharmacologies: MutableMap<String, Pharmacology> = mutableMapOf()

        operator fun get(pharmacologyUri: String?): Pharmacology? {
            if (pharmacologyUri == null) return null

            return pharmacologies[pharmacologyUri]
        }

        init {
            val sparqlConnector = SparqlConnector()


            val query = """
           PREFIX n: <https://nap.nprod.net/>
           SELECT ?pharmacology ?code ?name {
                ?pharmacology a n:pharmacology;
                             n:name ?name.
            }
        """.trimIndent()

            val result = sparqlConnector.getResultsOfQuery(query)
            if (result != null) {
                while (result.hasNext()) {
                    val solution = result.nextSolution()
                    val pharmacologyUri = solution["pharmacology"].asResource().uri
                    val name = solution["name"].asLiteral().string
                    pharmacologies[pharmacologyUri] = Pharmacology(uri = pharmacologyUri, name = name)
                }
            }
        }
    }
}
