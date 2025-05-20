package net.nprod.nap.rdf

import net.nprod.nap.types.Compound

/**
 * Get all compounds that have the specified synonym
 *
 * @param uri The URI of the compound synonym
 * @param sparqlConnector The SPARQL connector
 * @return A list of compounds that have the given synonym
 */
fun compoundsOfSynonym(
    uri: String,
    sparqlConnector: SparqlConnector
): List<Compound> {
    val sparql = """
        PREFIX n: <https://nap.nprod.net/>
        SELECT DISTINCT ?compound ?compoundName ?compoundClass ?compoundCode ?compoundCodeName
        WHERE {
            ?compound n:synonym <$uri>.
            ?compound a n:compound.
            ?compound n:name ?compoundName.
            OPTIONAL {
                ?compound n:compoundclass ?compoundClass.
            }
            OPTIONAL {
                ?compound n:has_compoundcode ?compoundCode. 
                ?compoundCode n:name ?compoundCodeName.
            }
        }
        ORDER BY ?compoundName
    """.trimIndent()
    
    val results = sparqlConnector.getResultsOfQuery(sparql)
    
    val compounds = mutableListOf<Compound>()
    
    if (results != null) {
        while (results.hasNext()) {
            val solution = results.nextSolution()
            val compoundUri = solution.getResource("compound")?.uri ?: continue
            val compoundName = solution.getLiteral("compoundName")?.string ?: continue
            val compoundClass = solution.getLiteral("compoundClass")?.string
            
            val compound = Compound(uri = compoundUri, name = compoundName, compoundClass = compoundClass)
            
            // Add compound code if present
            val compoundCodeResource = solution.getResource("compoundCode")
            val compoundCodeName = solution.getLiteral("compoundCodeName")
            
            if (compoundCodeResource != null && compoundCodeName != null) {
                compound.compoundCode = net.nprod.nap.types.CompoundCode(
                    compoundCodeResource.uri,
                    compoundCodeName.string
                )
            }
            
            compounds.add(compound)
        }
    }
    
    return compounds
}

/**
 * Get a compound synonym by URI
 *
 * @param uri The URI of the compound synonym
 * @param sparqlConnector The SPARQL connector
 * @return The compound synonym with the given URI
 */
fun getSynonymDetails(
    uri: String,
    sparqlConnector: SparqlConnector
): net.nprod.nap.types.CompoundSynonym? {
    val sparql = """
        PREFIX n: <https://nap.nprod.net/>
        SELECT ?name
        WHERE {
            <$uri> n:name ?name.
        }
    """.trimIndent()
    
    val results = sparqlConnector.getResultsOfQuery(sparql)
    
    if (results != null && results.hasNext()) {
        val solution = results.nextSolution()
        val name = solution.getLiteral("name")?.string ?: return null
        return net.nprod.nap.types.CompoundSynonym(uri, name)
    }
    
    return null
}