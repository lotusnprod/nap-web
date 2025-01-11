package net.nprod.nap.types

import net.nprod.nap.rdf.SparqlConnector

data class Animal (
    val uri: String,
    val name: String
) {
    object Cache {
        private val animals: MutableMap<String, Animal> = mutableMapOf()

        operator fun get(animalUri: String?): Animal? {
            if (animalUri == null) return null

            return animals[animalUri]
        }

        init {
            val sparqlConnector = SparqlConnector()


            val query = """
           PREFIX n: <https://nap.nprod.net/>
           SELECT ?animal ?code ?name {
                ?animal a n:animal;
                             n:name ?name.
            }
        """.trimIndent()

            val result = sparqlConnector.getResultsOfQuery(query)
            if (result != null) {
                while (result.hasNext()) {
                    val solution = result.nextSolution()
                    val animalUri = solution["animal"].asResource().uri
                    val name = solution["name"].asLiteral().string
                    animals[animalUri] = Animal(uri = animalUri, name = name)
                }
            }
        }
    }
}
