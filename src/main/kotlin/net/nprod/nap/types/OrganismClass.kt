package net.nprod.nap.types

import kotlinx.serialization.Serializable
import net.nprod.nap.rdf.SparqlConnector

@Serializable
data class OrganismClass(
    var uri: String,
    var name: String
)  {
    object Cache : ReferenceCache<OrganismClass>() {
        override fun load(sparqlConnector: SparqlConnector): Map<String, OrganismClass> {
            val organismClasses = mutableMapOf<String, OrganismClass>()

            val query = """
           PREFIX n: <https://nap.nprod.net/>
           SELECT ?organismClass ?name {
                ?organismClass a n:organismclass;
                             n:name ?name.
            }
        """.trimIndent()

            val result = sparqlConnector.getResultsOfQuery(query, logQuery = false)
            if (result != null) {
                while (result.hasNext()) {
                    val solution = result.nextSolution()
                    val organismClassUri = solution["organismClass"].asResource().uri
                    val name = solution["name"].asLiteral().string
                    organismClasses[organismClassUri] = OrganismClass(uri = organismClassUri, name = name)
                }
            }
            return organismClasses
        }
    }
}
