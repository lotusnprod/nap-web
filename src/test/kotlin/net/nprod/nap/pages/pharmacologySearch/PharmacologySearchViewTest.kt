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
        assertContains(html, "<th>Activity</th>")
        
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
    fun testNoQueryListsEveryActivityInsteadOfAskingForATerm() {
        val viewData = PharmacologySearchViewData(
            query = null,
            pharmacologyEntries = listOf(
                mapOf("uri" to "https://nap.nprod.net/pharmacology/1", "name" to "ANTIBACTERIAL", "experiments" to "12"),
                mapOf("uri" to "https://nap.nprod.net/pharmacology/2", "name" to "ANTIVIRAL", "experiments" to "0")
            )
        )

        val html = PharmacologySearchView.render(viewData)

        // The activity names are a vocabulary: reading through it is the point of the page
        assertContains(html, "Pharmacological activities")
        assertFalse(html.contains("Please enter a search term."))
        assertContains(html, "<a href=\"/pharmacology/1\">ANTIBACTERIAL</a>")
        assertContains(html, "<a href=\"/pharmacology/2\">ANTIVIRAL</a>")
        assertContains(html, "Showing ")
    }

    @Test
    fun testTheListingFiltersOnTheActivityNameAlone() {
        val viewData = PharmacologySearchViewData(
            query = null,
            pharmacologyEntries = listOf(
                mapOf("uri" to "https://nap.nprod.net/pharmacology/1", "name" to "ANTIBACTERIAL", "experiments" to "12")
            )
        )

        val html = PharmacologySearchView.render(viewData)

        // Filtering on the whole row would let "12" match on the experiment count
        assertContains(html, """data-filter="ANTIBACTERIAL"""")
        assertContains(html, "/assets/js/live-filter.js")
    }

    @Test
    fun testTheExperimentCountIsShown() {
        val viewData = PharmacologySearchViewData(
            query = "anti",
            pharmacologyEntries = listOf(
                mapOf("uri" to "https://nap.nprod.net/pharmacology/1", "name" to "ANTIBACTERIAL", "experiments" to "12")
            )
        )

        val html = PharmacologySearchView.render(viewData)

        assertContains(html, "<th class=\"text-end\">Experiments</th>")
        assertContains(html, "<td class=\"text-end\">12</td>")
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