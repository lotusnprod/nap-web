package net.nprod.nap.pages

import kotlin.test.*
import kotlinx.html.*
import org.apache.jena.query.ResultSet

class GenericSearchPageTest {
    
    @Test
    fun testGenericSearchPageWithNullQuery() {
        val html = genericSearchPage(
            entityType = "Compound",
            query = null,
            searchQueryFunction = null,
            processResults = null,
            renderTableHeaders = { th { +"Name" } },
            renderTableRow = { td { +"Test" } }
        )
        
        assertContains(html, "Compound Search")
        assertContains(html, "Please enter a search term.")
        assertFalse(html.contains("Search Results"))
    }
    
    @Test
    fun testGenericSearchPageWithEmptyQuery() {
        val html = genericSearchPage(
            entityType = "Organism",
            query = "",
            searchQueryFunction = null,
            processResults = null,
            renderTableHeaders = { th { +"Name" } },
            renderTableRow = { td { +"Test" } }
        )
        
        assertContains(html, "Organism Search")
        assertContains(html, "Please enter a search term.")
        assertFalse(html.contains("Search Results"))
    }
    
    @Test
    fun testGenericSearchPageWithBlankQuery() {
        val html = genericSearchPage(
            entityType = "Pharmacy",
            query = "   ",
            searchQueryFunction = null,
            processResults = null,
            renderTableHeaders = { th { +"Name" } },
            renderTableRow = { td { +"Test" } }
        )
        
        assertContains(html, "Pharmacy Search")
        assertContains(html, "Please enter a search term.")
        assertFalse(html.contains("Search Results"))
    }
    
    @Test
    fun testGenericSearchPageWithPreProcessedResults() {
        val preProcessedResults = listOf(
            mapOf("name" to "Aspirin", "uri" to "/compound/1"),
            mapOf("name" to "Caffeine", "uri" to "/compound/2"),
            mapOf("name" to "Paracetamol", "uri" to "/compound/3")
        )
        
        val html = genericSearchPage(
            entityType = "Compound",
            query = "test",
            searchQueryFunction = null,
            processResults = null,
            renderTableHeaders = { 
                th { +"Name" }
                th { +"Link" }
            },
            renderTableRow = { entity ->
                td { +(entity["name"] ?: "") }
                td { 
                    a(href = entity["uri"]) { +"View" }
                }
            },
            preProcessedResults = preProcessedResults
        )
        
        assertContains(html, "Compound Search: test")
        assertContains(html, "Found 3 results.")
        assertContains(html, "Search Results")
        assertContains(html, "Aspirin")
        assertContains(html, "Caffeine")
        assertContains(html, "Paracetamol")
        assertContains(html, "/compound/1")
        assertContains(html, "/compound/2")
        assertContains(html, "/compound/3")
    }
    
    @Test
    fun testGenericSearchPageWithEmptyResults() {
        val html = genericSearchPage(
            entityType = "Citation",
            query = "nonexistent",
            searchQueryFunction = null,
            processResults = null,
            renderTableHeaders = { th { +"Title" } },
            renderTableRow = { td { +"Should not appear" } },
            preProcessedResults = emptyList()
        )
        
        assertContains(html, "Citation Search: nonexistent")
        assertContains(html, "Found 0 results.")
        assertContains(html, "No citation found matching 'nonexistent'.")
        assertFalse(html.contains("Should not appear"))
    }
    
    @Test
    fun testGenericSearchPageWithCustomHeaderColor() {
        val html = genericSearchPage(
            entityType = "Alert",
            query = "toxic",
            searchQueryFunction = null,
            processResults = null,
            renderTableHeaders = { th { +"Alert" } },
            renderTableRow = { td { +"Toxic" } },
            headerColor = "bg-danger",
            preProcessedResults = listOf(mapOf("name" to "Toxic"))
        )
        
        assertContains(html, "bg-danger text-white")
        assertContains(html, "Alert Search: toxic")
        assertContains(html, "Toxic")
    }
    
    @Test
    fun testGenericSearchPageWithAdditionalInfoBlock() {
        val preProcessedResults = listOf(
            mapOf("type" to "Plant"),
            mapOf("type" to "Fungus"),
            mapOf("type" to "Plant")
        )
        
        val html = genericSearchPage(
            entityType = "Organism",
            query = "species",
            searchQueryFunction = null,
            processResults = null,
            renderTableHeaders = { th { +"Type" } },
            renderTableRow = { entity -> td { +(entity["type"] ?: "") } },
            additionalInfoBlock = { results ->
                div("alert alert-info") {
                    val plantCount = results.count { it["type"] == "Plant" }
                    val fungusCount = results.count { it["type"] == "Fungus" }
                    p { +"Distribution: $plantCount plants, $fungusCount fungi" }
                }
            },
            preProcessedResults = preProcessedResults
        )
        
        assertContains(html, "Organism Search: species")
        assertContains(html, "Found 3 results.")
        assertContains(html, "alert alert-info")
        assertContains(html, "Distribution: 2 plants, 1 fungi")
    }
    
    @Test
    fun testGenericSearchPageTableStructure() {
        val preProcessedResults = listOf(
            mapOf("id" to "1", "name" to "Item 1", "value" to "100"),
            mapOf("id" to "2", "name" to "Item 2", "value" to "200")
        )
        
        val html = genericSearchPage(
            entityType = "Test",
            query = "search",
            searchQueryFunction = null,
            processResults = null,
            renderTableHeaders = { 
                th { +"ID" }
                th { +"Name" }
                th { +"Value" }
            },
            renderTableRow = { entity ->
                td { +(entity["id"] ?: "") }
                td { +(entity["name"] ?: "") }
                td { +(entity["value"] ?: "") }
            },
            preProcessedResults = preProcessedResults
        )
        
        // Check table structure
        assertContains(html, "<table")
        assertContains(html, "table table-striped table-hover")
        assertContains(html, "<thead>")
        assertContains(html, "<tbody>")
        assertContains(html, "<th>ID</th>")
        assertContains(html, "<th>Name</th>")
        assertContains(html, "<th>Value</th>")
        assertContains(html, "Item 1")
        assertContains(html, "Item 2")
        assertContains(html, "100")
        assertContains(html, "200")
    }
    
    @Test
    fun testGenericSearchPageWithComplexData() {
        val preProcessedResults = listOf(
            mapOf(
                "name" to "Aspergillus niger",
                "family" to "Aspergillaceae",
                "uri" to "/organism/1",
                "citation" to "Smith et al. 2023"
            )
        )
        
        val html = genericSearchPage(
            entityType = "Organism",
            query = "aspergillus",
            searchQueryFunction = null,
            processResults = null,
            renderTableHeaders = { 
                th { +"Scientific Name" }
                th { +"Family" }
                th { +"Citation" }
                th { +"Actions" }
            },
            renderTableRow = { entity ->
                td { 
                    a(href = entity["uri"]) { 
                        +(entity["name"] ?: "Unknown")
                    }
                }
                td { +(entity["family"] ?: "") }
                td { +(entity["citation"] ?: "") }
                td { 
                    button(classes = "btn btn-sm btn-primary") { 
                        +"View Details" 
                    }
                }
            },
            preProcessedResults = preProcessedResults
        )
        
        assertContains(html, "Organism Search: aspergillus")
        assertContains(html, "Aspergillus niger")
        assertContains(html, "Aspergillaceae")
        assertContains(html, "Smith et al. 2023")
        assertContains(html, "/organism/1")
        assertContains(html, "btn btn-sm btn-primary")
        assertContains(html, "View Details")
    }
    
    @Test
    fun testGenericSearchPageBootstrapStructure() {
        val html = genericSearchPage(
            entityType = "Test",
            query = "test",
            searchQueryFunction = null,
            processResults = null,
            renderTableHeaders = { th { +"Header" } },
            renderTableRow = { td { +"Row" } },
            preProcessedResults = listOf(mapOf("test" to "test"))
        )
        
        // Check Bootstrap structure
        assertContains(html, "container")
        assertContains(html, "row")
        assertContains(html, "col-12")
        assertContains(html, "card mb-4")
        assertContains(html, "card-header")
        assertContains(html, "card-body p-0")
        assertContains(html, "table-responsive")
    }
    
    @Test
    fun testGenericSearchPageWithSpecialCharactersInQuery() {
        val html = genericSearchPage(
            entityType = "Compound",
            query = "C₆H₁₂O₆ & <test>",
            searchQueryFunction = null,
            processResults = null,
            renderTableHeaders = { th { +"Name" } },
            renderTableRow = { td { +"Test" } },
            preProcessedResults = emptyList()
        )
        
        // HTML should properly escape special characters
        assertContains(html, "Compound Search: C₆H₁₂O₆ &amp; &lt;test&gt;")
        assertContains(html, "No compound found matching 'C₆H₁₂O₆ &amp; &lt;test&gt;'.")
    }
}