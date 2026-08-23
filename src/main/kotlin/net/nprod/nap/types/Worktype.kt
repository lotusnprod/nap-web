package net.nprod.nap.types

import kotlinx.serialization.Serializable
import net.nprod.nap.rdf.SparqlConnector

@Serializable
data class Worktype (
    val uri: String,
    val code: String,
    val name: String,
    val group: WorktypeGroup? = null
) {
    object Cache : ReferenceCache<Worktype>() {
        override fun load(sparqlConnector: SparqlConnector): Map<String, Worktype> {
            val worktypes = mutableMapOf<String, Worktype>()

            val query = """
           PREFIX n: <https://nap.nprod.net/>
           SELECT ?worktype ?code ?name ?group {
                ?worktype a n:worktype;
                         n:code ?code;
                         n:name ?name.
                OPTIONAL { ?worktype n:has_group ?group. }
            }
        """.trimIndent()

            val result = sparqlConnector.getResultsOfQuery(query, logQuery = false)
            if (result != null) {
                while (result.hasNext()) {
                    val solution = result.nextSolution()
                    val worktypeUri = solution["worktype"].asResource().uri
                    val code = solution["code"].asLiteral().string
                    val name = solution["name"].asLiteral().string
                    val groupUri = if (solution.contains("group")) solution["group"]?.asResource()?.uri else null
                    val group = groupUri?.let { WorktypeGroup.Cache[it] }
                    worktypes[worktypeUri] = Worktype(uri = worktypeUri, code = code, name = name, group = group)
                }
            }
            return worktypes
        }
    }
}
