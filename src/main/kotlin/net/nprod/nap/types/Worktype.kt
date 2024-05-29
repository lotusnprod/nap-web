package net.nprod.nap.types

import net.nprod.nap.rdf.SparqlConnector

data class Worktype (
    val uri: String,
    val code: String,
    val name: String
) {
    object Cache {
        private val worktypes: MutableMap<String, Worktype> = mutableMapOf()

        operator fun get(worktypeUri: String?): Worktype? {
            if (worktypeUri == null) return null

            return worktypes[worktypeUri]
        }

        init {
            val sparqlConnector = SparqlConnector()


            val query = """
           PREFIX n: <https://nap.nprod.net/>
           SELECT ?worktype ?code ?name {
                ?worktype a n:worktype;
                             n:code ?code;
                             n:name ?name.
            }
        """.trimIndent()

            val result = sparqlConnector.getResultsOfQuery(query)
            if (result != null) {
                while (result.hasNext()) {
                    val solution = result.nextSolution()
                    val worktypeUri = solution["worktype"].asResource().uri
                    val code = solution["code"].asLiteral().string
                    val name = solution["name"].asLiteral().string
                    worktypes[worktypeUri] = Worktype(uri = worktypeUri, code = code, name = name)
                }
            }
        }
    }
}
