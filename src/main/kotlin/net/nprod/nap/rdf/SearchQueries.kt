package net.nprod.nap.rdf

import io.ktor.util.toLowerCasePreservingASCIIRules

/**
 * Contains SPARQL queries for searching different entity types
 */

/**
 * Clean a search query for safe use in SPARQL text search
 * @param query The raw query string to clean
 * @return A cleaned query string safe for use in SPARQL text search
 */
fun cleanSearchQuery(query: String): String {
    // First handle backslash escaping as we'll be adding backslashes for other characters
    var cleanedQuery = query.replace("\\", "\\\\")
    
    // Convert to lowercase
    cleanedQuery = cleanedQuery.toLowerCasePreservingASCIIRules()
    
    // Escape special characters for Lucene/text search (excluding backslash which we already handled)
    val specialChars = """+-&|!(){}[]^"~*?:/"""
    
    // Escape each special character with a preceding backslash
    specialChars.forEach { char ->
        cleanedQuery = cleanedQuery.replace(char.toString(), "\\\\$char")
    }

    // Then we escape quote signs
    cleanedQuery = cleanedQuery.replace("\"", "\\\"")
    
    return cleanedQuery
}

/**
 * Get a SPARQL query to search for compounds by name using text indexing
 * @param query The search term to find in compound names
 * @return A SPARQL query string to search for compounds
 */
fun compoundSearchQuery(query: String): String {
    val cleanQuery = cleanSearchQuery(query)
    return """
        PREFIX n: <https://nap.nprod.net/>
        PREFIX text: <http://jena.apache.org/text#>
        SELECT DISTINCT ?compound ?name ?compoundClass ?number
        WHERE {
            ?compound text:query (n:name "$cleanQuery").
            ?compound a n:compound;
                      n:name ?name;
                      n:compoundclass ?compoundClass;
                      n:number ?number.
        }
        ORDER BY ?name
    """.trimIndent()
}

/**
 * Get a SPARQL query to search for organisms by name using text indexing
 * @param query The search term to find in organism names
 * @return A SPARQL query string to search for organisms
 */
fun organismSearchQuery(query: String): String {
    val cleanQuery = cleanSearchQuery(query)
    return """
        PREFIX n: <https://nap.nprod.net/>
        PREFIX text: <http://jena.apache.org/text#>
        SELECT DISTINCT ?organism ?genusname ?speciesname ?subspeciesname ?familyname ?number ?taxon
        WHERE {
            ?organism text:query "$cleanQuery".
            ?organism a n:organism;
                      n:number ?number.
            OPTIONAL { ?organism n:genusname ?genusname }
            OPTIONAL { ?organism n:speciesname ?speciesname }
            OPTIONAL { ?organism n:subspeciesname ?subspeciesname }
            OPTIONAL { ?organism n:familyname ?familyname }
            OPTIONAL { ?organism n:has_taxon ?taxon }
        }
        ORDER BY ?genusname ?speciesname
    """.trimIndent()
}

/**
 * Get a SPARQL query to search for pharmacology entries by name using text indexing
 * @param query The search term to find in pharmacology entries
 * @return A SPARQL query string to search for pharmacology entries
 */
fun pharmacologySearchQuery(query: String): String {
    val cleanQuery = cleanSearchQuery(query)
    return """
        PREFIX n: <https://nap.nprod.net/>
        PREFIX text: <http://jena.apache.org/text#>
        SELECT DISTINCT ?pharmacology ?name
        WHERE {
            ?pharmacology text:query "$cleanQuery".
            ?pharmacology a n:pharmacology;
                         n:name ?name.
        }
        ORDER BY ?name
    """.trimIndent()
}