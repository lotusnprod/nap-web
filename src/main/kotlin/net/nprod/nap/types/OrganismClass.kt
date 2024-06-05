package net.nprod.nap.types

import net.nprod.nap.rdf.SparqlConnector

data class OrganismClass(
    var uri: String,
    var name: String
)  {
    object Cache {
        private val organismClasses: MutableMap<String, OrganismClass> = mutableMapOf()

        operator fun get(organismClassUri: String?): OrganismClass? {
            if (organismClassUri == null) return null

            return organismClasses[organismClassUri]
        }

        init {
            val sparqlConnector = SparqlConnector()


            val query = """
           PREFIX n: <https://nap.nprod.net/>
           SELECT ?organismClass ?name {
                ?organismClass a n:organismclass;
                             n:name ?name.
            }
        """.trimIndent()

            val result = sparqlConnector.getResultsOfQuery(query)
            if (result != null) {
                while (result.hasNext()) {
                    val solution = result.nextSolution()
                    val organismClassUri = solution["organismClass"].asResource().uri
                    val name = solution["name"].asLiteral().string
                    organismClasses[organismClassUri] = OrganismClass(uri = organismClassUri, name = name)
                }
            }
        }
    }
}
