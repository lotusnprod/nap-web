package net.nprod.nap.types

import kotlinx.serialization.Serializable
import net.nprod.nap.rdf.SparqlConnector

@Serializable
data class Citation(
    val uri: String,
    var title: String? = null,
    var authors: String? = null,
    var year: String? = null,
    var journal: String? = null,
    var volume: String? = null,
    var issue: String? = null,
    var pages: String? = null,
    var doi: String? = null
) {
    companion object {
        fun fromSparql(sparqlConnector: SparqlConnector, uri: String): Citation {
            var citation = Citation(uri)
            
            val query = """
            PREFIX n: <https://nap.nprod.net/>
            SELECT ?citation ?title ?authors ?year ?journal ?volume ?issue ?pages ?doi
            WHERE {
                ?citation a n:citation.
                OPTIONAL { ?citation n:title ?title. }
                OPTIONAL { ?citation n:authors ?authors. }
                OPTIONAL { ?citation n:year ?year. }
                OPTIONAL { ?citation n:journal ?journal. }
                OPTIONAL { ?citation n:volume ?volume. }
                OPTIONAL { ?citation n:issue ?issue. }
                OPTIONAL { ?citation n:pages ?pages. }
                OPTIONAL { ?citation n:doi ?doi. }
                
                VALUES ?citation { <$uri> }
            }
            """.trimIndent()
            
            val result = sparqlConnector.getResultsOfQuery(query, logQuery = false)
            if (result != null) {
                while (result.hasNext()) {
                    val solution = result.nextSolution()
                    citation.title = solution["title"]?.asLiteral()?.string
                    citation.authors = solution["authors"]?.asLiteral()?.string
                    citation.year = solution["year"]?.asLiteral()?.string
                    citation.journal = solution["journal"]?.asLiteral()?.string
                    citation.volume = solution["volume"]?.asLiteral()?.string
                    citation.issue = solution["issue"]?.asLiteral()?.string
                    citation.pages = solution["pages"]?.asLiteral()?.string
                    citation.doi = solution["doi"]?.asLiteral()?.string
                }
            }
            
            return citation
        }
    }
    
    fun formatCitation(): String {
        val parts = mutableListOf<String>()
        
        authors?.let { parts.add(it) }
        year?.let { parts.add("($it)") }
        title?.let { parts.add(it) }
        
        val journalInfo = mutableListOf<String>()
        journal?.let { journalInfo.add(it) }
        
        val volumeIssue = mutableListOf<String>()
        volume?.let { volumeIssue.add(it) }
        issue?.let { volumeIssue.add("($it)") }
        
        if (volumeIssue.isNotEmpty()) {
            journalInfo.add(volumeIssue.joinToString(""))
        }
        
        pages?.let { journalInfo.add(it) }
        
        if (journalInfo.isNotEmpty()) {
            parts.add(journalInfo.joinToString(", "))
        }
        
        return parts.joinToString(". ")
    }
}