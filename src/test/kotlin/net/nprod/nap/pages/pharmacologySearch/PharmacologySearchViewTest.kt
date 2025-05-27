package net.nprod.nap.pages.pharmacologySearch

import kotlin.test.*

class PharmacologySearchViewTest {
    
    @Test
    fun testRenderWithEmptyQuery() {
        val viewData = PharmacologySearchViewData(
            query = null,
            pharmacologyEntries = emptyList()
        )
        
        val html = PharmacologySearchView.render(viewData)
        
        assertContains(html, "Pharmacology Search")
        assertContains(html, "Please enter a search term.")
        assertFalse(html.contains("Search Results"))
    }
    
    @Test
    fun testRenderWithQueryAndNoResults() {
        val viewData = PharmacologySearchViewData(
            query = "nonexistent",
            pharmacologyEntries = emptyList()
        )
        
        val html = PharmacologySearchView.render(viewData)
        
        assertContains(html, "Pharmacology Search: nonexistent")
        assertContains(html, "Found 0 results.")
        assertContains(html, "No pharmacology found matching 'nonexistent'.")
    }
    
    @Test
    fun testRenderWithQueryAndResults() {
        val pharmacologyEntries = listOf(
            mapOf(
                "uri" to "https://nap.nprod.net/pharmacology/antimicrobial",
                "name" to "Antimicrobial"
            ),
            mapOf(
                "uri" to "https://nap.nprod.net/pharmacology/antiviral",
                "name" to "Antiviral"
            ),
            mapOf(
                "uri" to "https://nap.nprod.net/pharmacology/anticancer",
                "name" to "Anticancer"
            )
        )
        
        val viewData = PharmacologySearchViewData(
            query = "anti",
            pharmacologyEntries = pharmacologyEntries
        )
        
        val html = PharmacologySearchView.render(viewData)
        
        assertContains(html, "Pharmacology Search: anti")
        assertContains(html, "Found 3 results.")
        assertContains(html, "Search Results")
        assertContains(html, "Antimicrobial")
        assertContains(html, "Antiviral")
        assertContains(html, "Anticancer")
        assertContains(html, "/pharmacology/antimicrobial")
        assertContains(html, "/pharmacology/antiviral")
        assertContains(html, "/pharmacology/anticancer")
    }
    
    @Test
    fun testRenderWithSpecialCharactersInQuery() {
        val viewData = PharmacologySearchViewData(
            query = "test & <special>",
            pharmacologyEntries = emptyList()
        )
        
        val html = PharmacologySearchView.render(viewData)
        
        // Should properly escape special characters
        assertContains(html, "Pharmacology Search: test &amp; &lt;special&gt;")
        assertContains(html, "No pharmacology found matching 'test &amp; &lt;special&gt;'.")
    }
    
    @Test
    fun testRenderWithSingleResult() {
        val pharmacologyEntries = listOf(
            mapOf(
                "uri" to "https://nap.nprod.net/pharmacology/immunomodulatory",
                "name" to "Immunomodulatory"
            )
        )
        
        val viewData = PharmacologySearchViewData(
            query = "immune",
            pharmacologyEntries = pharmacologyEntries
        )
        
        val html = PharmacologySearchView.render(viewData)
        
        assertContains(html, "Pharmacology Search: immune")
        assertContains(html, "Found 1 results.")
        assertContains(html, "Immunomodulatory")
        assertContains(html, "/pharmacology/immunomodulatory")
    }
    
    @Test
    fun testRenderUsesCorrectHeaderColor() {
        val viewData = PharmacologySearchViewData(
            query = "test",
            pharmacologyEntries = listOf(
                mapOf("uri" to "https://nap.nprod.net/pharmacology/test", "name" to "Test")
            )
        )
        
        val html = PharmacologySearchView.render(viewData)
        
        // Should use bg-primary as specified in the view
        assertContains(html, "bg-primary text-white")
    }
    
    @Test
    fun testRenderTableStructure() {
        val pharmacologyEntries = listOf(
            mapOf(
                "uri" to "https://nap.nprod.net/pharmacology/hepatoprotective",
                "name" to "Hepatoprotective"
            ),
            mapOf(
                "uri" to "https://nap.nprod.net/pharmacology/neuroprotective",
                "name" to "Neuroprotective"
            )
        )
        
        val viewData = PharmacologySearchViewData(
            query = "protective",
            pharmacologyEntries = pharmacologyEntries
        )
        
        val html = PharmacologySearchView.render(viewData)
        
        // Check table structure
        assertContains(html, "<table")
        assertContains(html, "table table-striped table-hover")
        assertContains(html, "<thead>")
        assertContains(html, "<tbody>")
        assertContains(html, "<th>Name</th>")
        
        // Check links are rendered correctly
        assertContains(html, "<a href=\"/pharmacology/hepatoprotective\">Hepatoprotective</a>")
        assertContains(html, "<a href=\"/pharmacology/neuroprotective\">Neuroprotective</a>")
    }
    
    @Test
    fun testRenderWithLongPharmacologyName() {
        val pharmacologyEntries = listOf(
            mapOf(
                "uri" to "https://nap.nprod.net/pharmacology/complex",
                "name" to "Anti-inflammatory and immunomodulatory with potential anticancer activity"
            )
        )
        
        val viewData = PharmacologySearchViewData(
            query = "complex",
            pharmacologyEntries = pharmacologyEntries
        )
        
        val html = PharmacologySearchView.render(viewData)
        
        assertContains(html, "Anti-inflammatory and immunomodulatory with potential anticancer activity")
        assertContains(html, "/pharmacology/complex")
    }
    
    @Test
    fun testRenderWithEmptyStringQuery() {
        val viewData = PharmacologySearchViewData(
            query = "",
            pharmacologyEntries = emptyList()
        )
        
        val html = PharmacologySearchView.render(viewData)
        
        assertContains(html, "Pharmacology Search")
        assertContains(html, "Please enter a search term.")
    }
    
    @Test
    fun testRenderWithWhitespaceOnlyQuery() {
        val viewData = PharmacologySearchViewData(
            query = "   ",
            pharmacologyEntries = emptyList()
        )
        
        val html = PharmacologySearchView.render(viewData)
        
        assertContains(html, "Pharmacology Search")
        assertContains(html, "Please enter a search term.")
    }
}