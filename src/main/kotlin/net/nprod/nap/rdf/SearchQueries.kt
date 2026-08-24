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

private val WHITESPACE = Regex("\\s+")
private val NON_ALPHANUMERIC = Regex("[^a-z0-9]+")

/** The same collapsing as [normalizeForPhraseMatch], expressed for SPARQL's REPLACE */
private const val NON_ALPHANUMERIC_SPARQL = "[^a-z0-9]+"

/**
 * Split a search query into the words it is made of
 *
 * Double quotes are dropped rather than escaped: people quote to ask for an exact phrase,
 * which is what a multi-word search does here anyway, so the quotes carry no information
 * and would otherwise reach Lucene as escaped literal characters and match nothing.
 *
 * @param query The raw query string
 * @return The words of the query, in the order they were typed
 */
fun searchWords(query: String): List<String> =
    query.replace("\"", " ").trim().split(WHITESPACE).filter { it.isNotEmpty() }

/**
 * Lowercase a name and collapse every run of non-alphanumeric characters into one space
 *
 * Applied to both sides of a phrase comparison so that "Alepposide A" also matches
 * "alepposide-a": natural product series are written both ways.
 *
 * @param text The text to normalize
 * @return The normalized text
 */
fun normalizeForPhraseMatch(text: String): String =
    text.lowercase().replace(NON_ALPHANUMERIC, " ").trim()

/**
 * Pick the word to hand to the text index
 *
 * The index holds one document per literal, and a bare multi-word Lucene query matches
 * nothing at all — neither across two literals of the same organism nor within a single
 * compound name. So a multi-word query looks up its longest word, the most selective one,
 * and [phraseFilter] checks the rest in SPARQL.
 *
 * @param words The words of the query
 * @return The word to search the index for, empty when there is no word at all
 */
private fun indexWord(words: List<String>): String = words.maxByOrNull { it.length } ?: ""

/**
 * A FILTER keeping only the rows whose name contains the whole query as a phrase
 *
 * @param nameExpression A SPARQL expression producing the name to match against
 * @param words The words of the query
 * @return A FILTER clause, or an empty string when there is nothing left to check
 */
private fun phraseFilter(nameExpression: String, words: List<String>): String {
    if (words.size < 2) return ""
    val phrase = normalizeForPhraseMatch(words.joinToString(" "))
    if (phrase.isEmpty()) return ""
    // phrase is alphanumerics and spaces only, so it needs no SPARQL escaping
    return """FILTER(CONTAINS(REPLACE(LCASE($nameExpression), "$NON_ALPHANUMERIC_SPARQL", " "), "$phrase"))"""
}

/**
 * Get a SPARQL query to search for compounds by name using text indexing
 * @param query The search term to find in compound names
 * @return A SPARQL query string to search for compounds
 */
fun compoundSearchQuery(query: String): String {
    val words = searchWords(query)
    val cleanQuery = cleanSearchQuery(indexWord(words))
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
            ${phraseFilter("?name", words)}
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
    val words = searchWords(query)
    val cleanQuery = cleanSearchQuery(indexWord(words))
    // An organism has no single name literal: it is spelled across four fields, and the
    // index holds each of them as its own document. Rebuild the full name to match on.
    val fullName = """CONCAT(
                COALESCE(?familyname, ""), " ",
                COALESCE(?genusname, ""), " ",
                COALESCE(?speciesname, ""), " ",
                COALESCE(?subspeciesname, "")
            )"""
    return """
        PREFIX n: <https://nap.nprod.net/>
        PREFIX text: <http://jena.apache.org/text#>
        SELECT DISTINCT ?organism ?genusname ?speciesname ?subspeciesname ?familyname ?number ?taxon ?taxonName
        WHERE {
            ?organism text:query "$cleanQuery".
            ?organism a n:organism;
                      n:number ?number.
            OPTIONAL { ?organism n:genusname ?genusname }
            OPTIONAL { ?organism n:speciesname ?speciesname }
            OPTIONAL { ?organism n:subspeciesname ?subspeciesname }
            OPTIONAL { ?organism n:familyname ?familyname }
            # The taxon name has to be nested: with ?taxon unbound, a sibling OPTIONAL
            # would match every named resource in the store.
            OPTIONAL {
                ?organism n:has_taxon ?taxon.
                OPTIONAL { ?taxon n:name ?taxonName }
            }
            ${phraseFilter(fullName, words)}
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
    val words = searchWords(query)
    val cleanQuery = cleanSearchQuery(indexWord(words))
    return """
        PREFIX n: <https://nap.nprod.net/>
        PREFIX text: <http://jena.apache.org/text#>
        SELECT DISTINCT ?pharmacology ?name
        WHERE {
            ?pharmacology text:query "$cleanQuery".
            ?pharmacology a n:pharmacology;
                         n:name ?name.
            ${phraseFilter("?name", words)}
        }
        ORDER BY ?name
    """.trimIndent()
}