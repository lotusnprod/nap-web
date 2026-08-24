package net.nprod.nap.rdf

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SearchQueriesTest {

    @Test
    fun testCleanSearchQuery() {
        // Test normal text
        assertEquals("simple", cleanSearchQuery("Simple"))
        
        // Test special characters that need escaping
        assertEquals("compound\\\\+name", cleanSearchQuery("compound+name"))
        assertEquals("search\\\\-term", cleanSearchQuery("Search-term"))
        assertEquals("contains\\\\&operator", cleanSearchQuery("contains&operator"))
        assertEquals("with\\\\|pipe", cleanSearchQuery("with|pipe"))
        assertEquals("nested\\\\(parens\\\\)", cleanSearchQuery("Nested(parens)"))
        assertEquals("square\\\\[brackets\\\\]", cleanSearchQuery("Square[brackets]"))
        assertEquals("curly\\\\{braces\\\\}", cleanSearchQuery("Curly{braces}"))
        assertEquals("\\\\^caret\\\\^symbol", cleanSearchQuery("^caret^symbol"))
        assertEquals("quotes\\\\\\\"here\\\\\\\"", cleanSearchQuery("quotes\"here\""))
        assertEquals("question\\\\?mark", cleanSearchQuery("question?mark"))
        assertEquals("asterisk\\\\*wildcard", cleanSearchQuery("asterisk*wildcard"))
        assertEquals("tilde\\\\~symbol", cleanSearchQuery("tilde~symbol"))
        assertEquals("back\\\\slash", cleanSearchQuery("back\\slash"))
        assertEquals("\\\\:", cleanSearchQuery(":"))
        
        // Test a complex case with multiple special characters
        assertEquals(
            "complex\\\\(test\\\\)\\\\: with\\\\-many\\\\*special\\\\~\\\\\\\"chars\\\\\\\"\\\\&\\\\|\\\\^\\\\!\\\\{\\\\}\\\\[\\\\]\\\\",
            cleanSearchQuery("Complex(test): with-many*special~\"chars\"&|^!{}[]\\")
        )
    }
    
    @Test
    fun testCompoundSearchQuery() {
        val query = compoundSearchQuery("aspirin")
        
        // Verify the query contains the expected structure
        assert(query.contains("PREFIX n: <https://nap.nprod.net/>"))
        assert(query.contains("PREFIX text: <http://jena.apache.org/text#>"))
        assert(query.contains("?compound text:query (n:name \"aspirin\")"))
        assert(query.contains("?compound a n:compound"))
        assert(query.contains("n:name ?name"))
        assert(query.contains("n:compoundclass ?compoundClass"))
        assert(query.contains("n:number ?number"))
        assert(query.contains("ORDER BY ?name"))
    }
    
    @Test
    fun testCompoundSearchQueryWithSpecialCharacters() {
        val query = compoundSearchQuery("compound+name")
        
        // Verify special characters are properly escaped
        assert(query.contains("?compound text:query (n:name \"compound\\\\+name\")"))
    }
    
    @Test
    fun testOrganismSearchQuery() {
        val query = organismSearchQuery("arabidopsis")

        // Verify the query contains the expected structure
        assert(query.contains("PREFIX n: <https://nap.nprod.net/>"))
        assert(query.contains("PREFIX text: <http://jena.apache.org/text#>"))
        assert(query.contains("?org text:query \"arabidopsis\""))
        assert(query.contains("?org a n:organism"))
        assert(query.contains("n:number ?nb"))
        assert(query.contains("OPTIONAL { ?org n:genusname ?genus }"))
        assert(query.contains("OPTIONAL { ?org n:speciesname ?species }"))
        assert(query.contains("OPTIONAL { ?org n:subspeciesname ?subspecies }"))
        assert(query.contains("OPTIONAL { ?org n:familyname ?family }"))
        assert(query.contains("OPTIONAL { ?org n:organismclass/n:name ?class }"))
        assert(query.contains("n:has_taxon ?taxon"))
        assert(query.contains("OPTIONAL { ?taxon n:name ?taxonName }"))
    }

    @Test
    fun testOrganismSearchQueryReturnsOneRowPerTaxon() {
        val query = organismSearchQuery("arabidopsis")

        // Specimen records repeat the same species once per publication. Collapsing them
        // here rather than in the controller is what makes the experiment count whole.
        assert(query.contains("GROUP BY ?taxon ?taxonName"))
        assert(query.contains("(COUNT(DISTINCT ?pharmacy) AS ?experiments)"))
        assert(query.contains("(SAMPLE(?genus) AS ?genusname)"))
    }

    @Test
    fun testOrganismSearchQueryWithSpecialCharacters() {
        val query = organismSearchQuery("test(organism)")

        // Verify special characters are properly escaped
        assert(query.contains("?org text:query \"test\\\\(organism\\\\)\""))
    }
    
    @Test
    fun testPharmacologySearchQuery() {
        val query = pharmacologySearchQuery("antibacterial")
        
        // Verify the query contains the expected structure
        assert(query.contains("PREFIX n: <https://nap.nprod.net/>"))
        assert(query.contains("PREFIX text: <http://jena.apache.org/text#>"))
        assert(query.contains("?pharmacology text:query \"antibacterial\""))
        assert(query.contains("?pharmacology a n:pharmacology"))
        assert(query.contains("n:name ?name"))
        assert(query.contains("ORDER BY ?name"))
    }
    
    @Test
    fun testPharmacologySearchQueryWithSpecialCharacters() {
        val query = pharmacologySearchQuery("anti-cancer*")

        // Verify special characters are properly escaped
        assert(query.contains("?pharmacology text:query \"anti\\\\-cancer\\\\*\""))
    }

    @Test
    fun testSearchWordsDropsQuotesAndBlanks() {
        assertEquals(listOf("alepposide", "a"), searchWords("  alepposide   a "))
        assertEquals(listOf("Alepposide", "A"), searchWords("\"Alepposide A\""))
        assertEquals(emptyList(), searchWords("   "))
    }

    @Test
    fun testNormalizeForPhraseMatchCollapsesPunctuation() {
        assertEquals("alepposide a", normalizeForPhraseMatch("Alepposide-A"))
        assertEquals("alepposide a", normalizeForPhraseMatch("  Alepposide   A  "))
        assertEquals("amyrin acetate", normalizeForPhraseMatch("α-Amyrin acetate"))
    }

    @Test
    fun testSingleWordSearchesAreNotFiltered() {
        // One word is what the text index handles on its own; nothing to check afterwards
        listOf(compoundSearchQuery("alepposide"), organismSearchQuery("adonis"), pharmacologySearchQuery("euphoriant"))
            .forEach { assertFalse(it.contains("FILTER"), "a one-word search needs no FILTER: $it") }
    }

    @Test
    fun testMultiWordCompoundSearchLooksUpTheLongestWordAndChecksThePhrase() {
        val query = compoundSearchQuery("Alepposide A")

        // "alepposide" is the selective word; the index cannot match "alepposide a" at all
        assertTrue(query.contains("?compound text:query (n:name \"alepposide\")"), query)
        assertTrue(query.contains("\"alepposide a\""), query)
    }

    @Test
    fun testMultiWordCompoundSearchIgnoresQuotesAroundThePhrase() {
        assertEquals(compoundSearchQuery("Alepposide A"), compoundSearchQuery("\"Alepposide A\""))
    }

    @Test
    fun testMultiWordOrganismSearchMatchesOnTheWholeName() {
        val query = organismSearchQuery("Adonis aleppica")

        assertTrue(query.contains("?org text:query \"aleppica\""), query)
        // The name is spread over four fields, so the phrase is checked against all of them
        listOf("?family", "?genus", "?species", "?subspecies").forEach {
            assertTrue(query.contains("COALESCE($it, \"\")"), "the phrase check should cover $it")
        }
        assertTrue(query.contains("\"adonis aleppica\""), query)
    }

    @Test
    fun testMultiWordPharmacologySearchChecksThePhrase() {
        val query = pharmacologySearchQuery("euphoriant activity")

        assertTrue(query.contains("?pharmacology text:query \"euphoriant\""), query)
        assertTrue(query.contains("\"euphoriant activity\""), query)
    }
}