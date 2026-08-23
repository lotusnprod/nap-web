package net.nprod.nap.types

import kotlinx.serialization.Serializable
import net.nprod.nap.rdf.SparqlConnector

@Serializable
data class Pharmacology (
    val uri: String,
    val name: String
) {

    companion object {
        fun fromSparql(sparqlConnector: SparqlConnector, uri: String): Pharmacology {
            var new: Pharmacology? = null

            val query = """
            PREFIX n: <https://nap.nprod.net/>
              SELECT ?pharmacology ?name {
                  ?pharmacology a n:pharmacology;
                                n:name ?name.
                  VALUES ?pharmacology { <$uri> }
            }
        """.trimIndent()

            val result = sparqlConnector.getResultsOfQuery(query, logQuery = false)
            if (result != null) {
                while (result.hasNext()) {
                    val solution = result.nextSolution()
                    val pharmacologyUri = solution["pharmacology"].asResource().uri
                    if (new == null) {
                        new = Pharmacology(pharmacologyUri, solution["name"].asLiteral().string)
                    }
                }
            }
            if (new == null) throw EntityNotFoundException("pharmacology", uri)
            return new
        }
    }

    object Cache : ReferenceCache<Pharmacology>() {
        override fun load(sparqlConnector: SparqlConnector): Map<String, Pharmacology> {
            val pharmacologies = mutableMapOf<String, Pharmacology>()

            val query = """
           PREFIX n: <https://nap.nprod.net/>
           SELECT ?pharmacology ?code ?name {
                ?pharmacology a n:pharmacology;
                             n:name ?name.
            }
        """.trimIndent()

            val result = sparqlConnector.getResultsOfQuery(query, logQuery = false)
            if (result != null) {
                while (result.hasNext()) {
                    val solution = result.nextSolution()
                    val pharmacologyUri = solution["pharmacology"].asResource().uri
                    val name = solution["name"].asLiteral().string
                    pharmacologies[pharmacologyUri] = Pharmacology(uri = pharmacologyUri, name = name)
                }
            }
            return pharmacologies
        }
    }
}
