package net.nprod.nap.types

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import net.nprod.nap.test.withInMemoryFuseki
import net.nprod.nap.rdf.SparqlConnector
import kotlinx.coroutines.runBlocking

class CitationTest {

    @Test
    fun testFormatCitationWithAllFields() {
        val citation = Citation(
            uri = "http://example.com/citation/1",
            title = "Test Article Title",
            authors = "Smith, J. and Doe, A.",
            year = "2023",
            journal = "Test Journal",
            volume = "42",
            issue = "3",
            pages = "123-456",
            doi = "10.1234/test.doi"
        )
        
        val formatted = citation.formatCitation()
        assertEquals("Smith, J. and Doe, A.. (2023). Test Article Title. Test Journal, 42(3), 123-456", formatted)
    }

    @Test
    fun testFormatCitationWithMinimalFields() {
        val citation = Citation(
            uri = "http://example.com/citation/1",
            title = "Minimal Title"
        )
        
        val formatted = citation.formatCitation()
        assertEquals("Minimal Title", formatted)
    }

    @Test
    fun testFormatCitationWithVolumeNoIssue() {
        val citation = Citation(
            uri = "http://example.com/citation/1",
            title = "Test Title",
            journal = "Test Journal",
            volume = "10"
        )
        
        val formatted = citation.formatCitation()
        assertEquals("Test Title. Test Journal, 10", formatted)
    }

    @Test
    fun testFormatCitationWithIssueNoVolume() {
        val citation = Citation(
            uri = "http://example.com/citation/1",
            title = "Test Title",
            journal = "Test Journal",
            issue = "2"
        )
        
        val formatted = citation.formatCitation()
        assertEquals("Test Title. Test Journal, (2)", formatted)
    }

    @Test
    fun testFormatCitationWithPagesOnly() {
        val citation = Citation(
            uri = "http://example.com/citation/1",
            title = "Test Title",
            journal = "Test Journal",
            pages = "100-200"
        )
        
        val formatted = citation.formatCitation()
        assertEquals("Test Title. Test Journal, 100-200", formatted)
    }

    @Test
    fun testFormatCitationEmpty() {
        val citation = Citation(uri = "http://example.com/citation/1")
        
        val formatted = citation.formatCitation()
        assertEquals("", formatted)
    }

    @Test
    fun testFormatCitationWithAuthorAndYearOnly() {
        val citation = Citation(
            uri = "http://example.com/citation/1",
            authors = "Johnson, M.",
            year = "2020"
        )
        
        val formatted = citation.formatCitation()
        assertEquals("Johnson, M.. (2020)", formatted)
    }

    @Test
    fun testFormatCitationWithJournalOnly() {
        val citation = Citation(
            uri = "http://example.com/citation/1",
            title = "Research Paper",
            journal = "Science Today"
        )
        
        val formatted = citation.formatCitation()
        assertEquals("Research Paper. Science Today", formatted)
    }

    @Test
    fun testFromSparqlWithMissingData() = runBlocking {
        withInMemoryFuseki { server ->
            // Set the SPARQL_SERVER system property
            System.setProperty("SPARQL_SERVER", server.getSparqlEndpoint())
            
            // Test with URI that doesn't exist in the test data
            val sparqlConnector = SparqlConnector()
            val citation = Citation.fromSparql(sparqlConnector, "https://nap.nprod.net/citation/999")
            
            // Should return citation with only URI set when no data found
            assertEquals("https://nap.nprod.net/citation/999", citation.uri)
            assertEquals(null, citation.title)
            assertEquals(null, citation.authors)
            assertEquals(null, citation.year)
            assertEquals(null, citation.journal)
            assertEquals(null, citation.volume)
            assertEquals(null, citation.issue)
            assertEquals(null, citation.pages)
            assertEquals(null, citation.doi)
        }
    }

    @Test
    fun testFromSparqlWithRealData() = runBlocking {
        withInMemoryFuseki { server ->
            // Set the SPARQL_SERVER system property
            System.setProperty("SPARQL_SERVER", server.getSparqlEndpoint())
            
            // First, add a test citation to the dataset
            val testCitation = """
                @prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .
                @prefix n: <https://nap.nprod.net/> .
                
                <https://nap.nprod.net/citation/test1> rdf:type n:citation ;
                    n:title "Test Citation Title" ;
                    n:authors "Test Author" ;
                    n:year "2024" ;
                    n:journal "Test Journal" ;
                    n:volume "1" ;
                    n:issue "2" ;
                    n:pages "10-20" ;
                    n:doi "10.1234/test" .
            """
            
            server.loadN3Data(testCitation)
            
            val sparqlConnector = SparqlConnector()
            val citation = Citation.fromSparql(sparqlConnector, "https://nap.nprod.net/citation/test1")
            
            // Should have loaded all the data
            assertEquals("https://nap.nprod.net/citation/test1", citation.uri)
            assertEquals("Test Citation Title", citation.title)
            assertEquals("Test Author", citation.authors)
            assertEquals("2024", citation.year)
            assertEquals("Test Journal", citation.journal)
            assertEquals("1", citation.volume)
            assertEquals("2", citation.issue)
            assertEquals("10-20", citation.pages)
            assertEquals("10.1234/test", citation.doi)
        }
    }

    @Test
    fun testFromSparqlWithPartialData() = runBlocking {
        withInMemoryFuseki { server ->
            // Set the SPARQL_SERVER system property
            System.setProperty("SPARQL_SERVER", server.getSparqlEndpoint())
            
            // Add a citation with only some fields to test null branches
            val partialCitation = """
                @prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .
                @prefix n: <https://nap.nprod.net/> .
                
                <https://nap.nprod.net/citation/test2> rdf:type n:citation ;
                    n:title "Partial Citation" ;
                    n:year "2023" .
            """
            
            server.loadN3Data(partialCitation)
            
            val sparqlConnector = SparqlConnector()
            val citation = Citation.fromSparql(sparqlConnector, "https://nap.nprod.net/citation/test2")
            
            // Should have loaded only the provided fields
            assertEquals("https://nap.nprod.net/citation/test2", citation.uri)
            assertEquals("Partial Citation", citation.title)
            assertEquals(null, citation.authors)
            assertEquals("2023", citation.year)
            assertEquals(null, citation.journal)
            assertEquals(null, citation.volume)
            assertEquals(null, citation.issue)
            assertEquals(null, citation.pages)
            assertEquals(null, citation.doi)
        }
    }

    @Test
    fun testFormatCitationWithNoJournalInfo() {
        // Test the edge case where we have volume/issue but no journal
        val citation = Citation(
            uri = "http://example.com/citation/1",
            title = "Edge Case Title",
            volume = "5",
            issue = "3",
            pages = "50-60"
        )
        
        val formatted = citation.formatCitation()
        assertEquals("Edge Case Title. 5(3), 50-60", formatted)
    }

    @Test
    fun testFormatCitationWithOnlyPagesNoJournal() {
        // Test case with only pages, no journal/volume/issue
        val citation = Citation(
            uri = "http://example.com/citation/1",
            title = "Pages Only Title",
            pages = "100-150"
        )
        
        val formatted = citation.formatCitation()
        assertEquals("Pages Only Title. 100-150", formatted)
    }

}