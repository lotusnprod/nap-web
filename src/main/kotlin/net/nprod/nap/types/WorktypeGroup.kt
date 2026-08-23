package net.nprod.nap.types

import kotlinx.serialization.Serializable
import net.nprod.nap.rdf.SparqlConnector

@Serializable
data class WorktypeGroup(
    val uri: String,
    val code: String,
    val name: String
) {
    object Cache : ReferenceCache<WorktypeGroup>() {
        override fun load(sparqlConnector: SparqlConnector): Map<String, WorktypeGroup> {
            val worktypeGroups = mutableMapOf<String, WorktypeGroup>()

            val query = """
                PREFIX n: <https://nap.nprod.net/>
                SELECT ?worktypeGroup ?code ?name {
                    ?worktypeGroup a n:worktypegroup;
                                   n:code ?code;
                                   n:name ?name.
                }
            """.trimIndent()

            val result = sparqlConnector.getResultsOfQuery(query, logQuery = false)
            if (result != null) {
                while (result.hasNext()) {
                    val solution = result.nextSolution()
                    val worktypeGroupUri = solution["worktypeGroup"].asResource().uri
                    val code = solution["code"].asLiteral().string
                    val name = solution["name"].asLiteral().string
                    worktypeGroups[worktypeGroupUri] = WorktypeGroup(uri = worktypeGroupUri, code = code, name = name)
                }
            }
            return worktypeGroups
        }
    }
}