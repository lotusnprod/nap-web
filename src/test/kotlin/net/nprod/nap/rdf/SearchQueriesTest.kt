package net.nprod.nap.rdf

import kotlin.test.Test
import kotlin.test.assertEquals

class SearchQueriesTest {
    
    @Test
    fun testCleanSearchQuery() {
        // Test normal text
        assertEquals("simple", cleanSearchQuery("Simple"))
        
        // Test special characters that need escaping
        assertEquals("compound\\+name", cleanSearchQuery("compound+name"))
        assertEquals("search\\-term", cleanSearchQuery("Search-term"))
        assertEquals("contains\\&operator", cleanSearchQuery("contains&operator"))
        assertEquals("with\\|pipe", cleanSearchQuery("with|pipe"))
        assertEquals("nested\\(parens\\)", cleanSearchQuery("Nested(parens)"))
        assertEquals("square\\[brackets\\]", cleanSearchQuery("Square[brackets]"))
        assertEquals("curly\\{braces\\}", cleanSearchQuery("Curly{braces}"))
        assertEquals("\\^caret\\^symbol", cleanSearchQuery("^caret^symbol"))
        assertEquals("quotes\\\"here\\\"", cleanSearchQuery("quotes\"here\""))
        assertEquals("question\\?mark", cleanSearchQuery("question?mark"))
        assertEquals("asterisk\\*wildcard", cleanSearchQuery("asterisk*wildcard"))
        assertEquals("tilde\\~symbol", cleanSearchQuery("tilde~symbol"))
        assertEquals("back\\\\slash", cleanSearchQuery("back\\slash"))
        assertEquals("\\:", cleanSearchQuery(":"))
        
        // Test a complex case with multiple special characters
        assertEquals(
            "complex\\(test\\)\\: with\\-many\\*special\\~\\\"chars\\\"\\&\\|\\^\\!\\{\\}\\[\\]\\\\",
            cleanSearchQuery("Complex(test): with-many*special~\"chars\"&|^!{}[]\\")
        )
    }
}