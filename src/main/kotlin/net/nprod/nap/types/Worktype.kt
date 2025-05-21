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
    object Cache {
        private val worktypes: MutableMap<String, Worktype> = mutableMapOf()

        operator fun get(worktypeUri: String?): Worktype? {
            if (worktypeUri == null) return null

            return worktypes[worktypeUri]
        }

        init {
            val sparqlConnector = SparqlConnector()

            // Initialize WorktypeGroup cache first
            WorktypeGroup.Cache

            val query = """
           PREFIX n: <https://nap.nprod.net/>
           SELECT ?worktype ?code ?name ?group {
                ?worktype a n:worktype;
                         n:code ?code;
                         n:name ?name.
                OPTIONAL { ?worktype n:has_group ?group. }
            }
        """.trimIndent()

            val result = sparqlConnector.getResultsOfQuery(query)
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
        }
    }
}
