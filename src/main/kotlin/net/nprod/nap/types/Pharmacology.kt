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

    /**
     * How many experiments measured each activity, uri → count.
     *
     * The activity list is 4000 names long and a name alone says nothing about whether
     * anything was ever recorded for it, so the count is what makes the list navigable.
     * One grouped query over the experiments, cached like the reference tables: cheap
     * enough to keep, too slow to run on every page view.
     *
     * Activities nothing was recorded for are absent from the map, not zero.
     */
    object ExperimentCounts : ReferenceCache<Int>() {
        override fun load(sparqlConnector: SparqlConnector): Map<String, Int> {
            val counts = mutableMapOf<String, Int>()

            val query = """
            PREFIX n: <https://nap.nprod.net/>
            SELECT ?pharmacology (COUNT(*) AS ?count) {
                 ?pharmacy n:has_pharmacology ?pharmacology.
            }
            GROUP BY ?pharmacology
        """.trimIndent()

            val result = sparqlConnector.getResultsOfQuery(query, logQuery = false)
            if (result != null) {
                while (result.hasNext()) {
                    val solution = result.nextSolution()
                    counts[solution["pharmacology"].asResource().uri] = solution["count"].asLiteral().int
                }
            }
            return counts
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
