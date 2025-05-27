package net.nprod.nap.rdf

import kotlin.test.Test
import kotlin.test.assertEquals

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
        assert(query.contains("?organism text:query \"arabidopsis\""))
        assert(query.contains("?organism a n:organism"))
        assert(query.contains("n:number ?number"))
        assert(query.contains("OPTIONAL { ?organism n:genusname ?genusname }"))
        assert(query.contains("OPTIONAL { ?organism n:speciesname ?speciesname }"))
        assert(query.contains("OPTIONAL { ?organism n:subspeciesname ?subspeciesname }"))
        assert(query.contains("OPTIONAL { ?organism n:familyname ?familyname }"))
        assert(query.contains("OPTIONAL { ?organism n:has_taxon ?taxon }"))
        assert(query.contains("ORDER BY ?genusname ?speciesname"))
    }
    
    @Test
    fun testOrganismSearchQueryWithSpecialCharacters() {
        val query = organismSearchQuery("test(organism)")
        
        // Verify special characters are properly escaped
        assert(query.contains("?organism text:query \"test\\\\(organism\\\\)\""))
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
}