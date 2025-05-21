package net.nprod.nap.types

import kotlinx.serialization.Serializable
import net.nprod.nap.rdf.SparqlConnector

@Serializable
data class Gender (
    val uri: String,
    val code: String,
    val name: String
) {
    object Cache {
        private val genders: MutableMap<String, Gender> = mutableMapOf()

        operator fun get(genderUri: String?): Gender? {
            if (genderUri == null) return null

            return genders[genderUri]
        }

        init {
            val sparqlConnector = SparqlConnector()


            val query = """
           PREFIX n: <https://nap.nprod.net/>
           SELECT ?gender ?code ?name {
                ?gender a n:gender;
                             n:code ?code;
                             n:name ?name.
            }
        """.trimIndent()

            val result = sparqlConnector.getResultsOfQuery(query)
            if (result != null) {
                while (result.hasNext()) {
                    val solution = result.nextSolution()
                    val genderUri = solution["gender"].asResource().uri
                    val code = solution["code"].asLiteral().string
                    val name = solution["name"].asLiteral().string
                    genders[genderUri] = Gender(uri = genderUri, code = code, name = name)
                }
            }
        }
    }
}
