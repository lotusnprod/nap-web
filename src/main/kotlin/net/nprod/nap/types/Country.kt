package net.nprod.nap.types

import net.nprod.nap.rdf.SparqlConnector

data class Country(
    var uri: String,
    var name: String
) {
    companion object {
        fun fromSparql(sparqlConn: SparqlConnector, uri: String): Country? {
            val query = """
                PREFIX n: <https://nap.nprod.net/>
                
                SELECT ?name WHERE {
                    <$uri> n:name ?name .
                }
                """
            
            val result = sparqlConn.getResultsOfQuery(query)
            if (result != null && result.hasNext()) {
                val solution = result.nextSolution()
                val name = solution["name"].asLiteral().string
                return Country(uri = uri, name = name)
            }
            return null
        }
    }
}