package net.nprod.nap.types

import kotlinx.serialization.Serializable
import net.nprod.nap.rdf.SparqlConnector

@Serializable
data class Compound(
    var uri: String,
    var compoundClass: String? = null,
    var name: String? = null,
    var publicationCode: String? = null,
    var synonyms: MutableList<CompoundSynonym> = mutableListOf(),
    var number: Int? = null,
    var publicationName: String? = null,
    var compoundCode: CompoundCode? = null,
) {
    companion object {
        fun fromSparql(sparqlConnector: SparqlConnector, uri: String): Compound {
            var new: Compound? = null

            val query = """
            PREFIX n: <https://nap.nprod.net/>
            SELECT ?compound ?compoundClass ?name ?publicationCode ?publicationName ?synonym ?synonymName ?compoundCode ?compoundCodeName {
                ?compound a n:compound;
                          n:compoundclass ?compoundClass;
                          n:name ?name;
                          n:number ?number.
                OPTIONAL {
                    ?compound n:publicationCode ?publicationCode.
                }
                OPTIONAL {
                    ?compound n:publicationName ?publicationName.
                }
                OPTIONAL {
                    ?compound n:has_compoundcode ?compoundCode. 
                    ?compoundCode n:name ?compoundCodeName.
                }
                OPTIONAL {
                    ?compound n:synonym ?synonym.
                    ?synonym n:name ?synonymName.
                }
                VALUES ?compound { <$uri> }
            }
        """.trimIndent()

            val result = sparqlConnector.getResultsOfQuery(query)
            if (result != null) {
                while (result.hasNext()) {
                    val solution = result.nextSolution()
                    val compoundUri = solution["compound"].asResource().uri
                    if (new == null) {
                        new = Compound(compoundUri)
                        new.compoundClass = solution["compoundClass"]?.asLiteral()?.string
                        new.name = solution["name"].asLiteral().string
                        new.number = solution["number"]?.asLiteral()?.int
                        new.publicationCode = solution["publicationCode"]?.asLiteral()?.string
                        new.publicationName = solution["publicationName"]?.asLiteral()?.string
                        val compoundCode = solution["compoundCode"]?.asResource()?.uri
                        if (compoundCode != null) {
                            new.compoundCode = CompoundCode(compoundCode, solution["compoundCodeName"]?.asLiteral()?.string ?: "Unknown code")
                        }
                    }
                    val synonymUri = solution["synonym"]?.asResource()?.uri
                    val synonymName = solution["synonymName"]?.asLiteral()?.string
                    if (synonymUri != null && synonymName != null) {
                        new.synonyms.add(CompoundSynonym(synonymUri, synonymName))
                    }
                }
            }
            if (new == null) throw Exception ("No compound found for $uri")
            return new
        }
    }
}
