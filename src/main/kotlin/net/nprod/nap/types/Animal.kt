package net.nprod.nap.types

import kotlinx.serialization.Serializable
import net.nprod.nap.rdf.SparqlConnector

@Serializable
data class Animal (
    val uri: String,
    val name: String
) {
    object Cache : ReferenceCache<Animal>() {
        override fun load(sparqlConnector: SparqlConnector): Map<String, Animal> {
            val animals = mutableMapOf<String, Animal>()

            val query = """
           PREFIX n: <https://nap.nprod.net/>
           SELECT ?animal ?code ?name {
                ?animal a n:animal;
                             n:name ?name.
            }
        """.trimIndent()

            val result = sparqlConnector.getResultsOfQuery(query, logQuery = false)
            if (result != null) {
                while (result.hasNext()) {
                    val solution = result.nextSolution()
                    val animalUri = solution["animal"].asResource().uri
                    val name = solution["name"].asLiteral().string
                    animals[animalUri] = Animal(uri = animalUri, name = name)
                }
            }
            return animals
        }
    }
}
