package net.nprod.nap.pages.compound

import kotlin.test.Test
import kotlin.test.assertTrue
import net.nprod.nap.types.Compound
import net.nprod.nap.types.CompoundCode
import net.nprod.nap.types.CompoundSynonym
import net.nprod.nap.types.Pharmacy

class CompoundViewTest {
    
    @Test
    fun testRenderWithCompleteCompound() {
        val compound = Compound("https://nap.nprod.net/compound/123").apply {
            name = "Test Compound"
            compoundClass = "Alkaloid"
            publicationCode = "TC-123"
            publicationName = "Test Publication"
            compoundCode = CompoundCode("https://nap.nprod.net/compoundcode/1", "CODE-001")
            synonyms = mutableListOf(
                CompoundSynonym("https://nap.nprod.net/synonym/1", "Synonym 1"),
                CompoundSynonym("https://nap.nprod.net/synonym/2", "Synonym 2")
            )
        }
        
        val pharmacyResults = listOf(
            Pharmacy("https://nap.nprod.net/pharmacy/1"),
            Pharmacy("https://nap.nprod.net/pharmacy/2")
        )
        
        val data = CompoundViewData(
            identifier = "123",
            compound = compound,
            pharmacies = pharmacyResults
        )
        
        val html = CompoundView.render(data)
        
        // Check that all fields are rendered
        assertTrue(html.contains("Test Compound (123)"))
        assertTrue(html.contains("Compound Details"))
        assertTrue(html.contains("Alkaloid"))
        assertTrue(html.contains("TC-123"))
        assertTrue(html.contains("Test Publication"))
        assertTrue(html.contains("CODE-001"))
        assertTrue(html.contains("Synonyms"))
        assertTrue(html.contains("Synonym 1"))
        assertTrue(html.contains("Synonym 2"))
        assertTrue(html.contains("Experiments (2)"))
        assertTrue(html.contains("Summary"))
        assertTrue(html.contains("Compound class: Alkaloid"))
        assertTrue(html.contains("Experiments: 2"))
        assertTrue(html.contains("View Experiments"))
    }
    
    @Test
    fun testRenderWithMinimalCompound() {
        val compound = Compound("https://nap.nprod.net/compound/456").apply {
            name = "Minimal Compound"
            // No other fields set
        }
        
        val data = CompoundViewData(
            identifier = "456",
            compound = compound,
            pharmacies = emptyList()
        )
        
        val html = CompoundView.render(data)
        
        // Check basic rendering
        assertTrue(html.contains("Minimal Compound (456)"))
        assertTrue(html.contains("Compound Details"))
        
        // Should not contain synonym section when empty
        assertTrue(!html.contains("Synonyms"))
        
        // Check summary with unknown values
        assertTrue(html.contains("Compound class: Unknown"))
        assertTrue(html.contains("Experiments: 0"))
        assertTrue(html.contains("Experiments (0)"))
    }
    
    @Test
    fun testRenderWithNullableFields() {
        val compound = Compound("https://nap.nprod.net/compound/789").apply {
            name = "Partial Compound"
            compoundClass = "Terpenoid"
            // publicationCode is null
            // publicationName is null
            // compoundCode is null
        }
        
        val data = CompoundViewData(
            identifier = "789",
            compound = compound,
            pharmacies = listOf(Pharmacy("https://nap.nprod.net/pharmacy/3"))
        )
        
        val html = CompoundView.render(data)
        
        // Check that only non-null fields are rendered
        assertTrue(html.contains("Partial Compound (789)"))
        assertTrue(html.contains("Terpenoid"))
        
        // Should not contain null fields
        assertTrue(!html.contains("Publication code"))
        assertTrue(!html.contains("Publication name"))
        assertTrue(!html.contains("Compound code"))
        
        // Check summary
        assertTrue(html.contains("Compound class: Terpenoid"))
        assertTrue(html.contains("Experiments: 1"))
    }
    
    @Test
    fun testRenderWithEmptySynonyms() {
        val compound = Compound("https://nap.nprod.net/compound/999").apply {
            name = "No Synonyms"
            compoundClass = "Flavonoid"
            synonyms = mutableListOf()
        }
        
        val data = CompoundViewData(
            identifier = "999",
            compound = compound,
            pharmacies = emptyList()
        )
        
        val html = CompoundView.render(data)
        
        // Should not contain synonym section when empty
        assertTrue(!html.contains(">Synonyms<"), "HTML should not contain Synonyms header when list is empty")
        assertTrue(html.contains("No Synonyms"))
        assertTrue(html.contains("Flavonoid"))
    }
    
    @Test
    fun testRenderWithLargeSynonymList() {
        val compound = Compound("https://nap.nprod.net/compound/1000").apply {
            name = "Many Synonyms"
            synonyms = (1..10).map { i ->
                CompoundSynonym("https://nap.nprod.net/synonym/$i", "Synonym $i")
            }.toMutableList()
        }
        
        val data = CompoundViewData(
            identifier = "1000",
            compound = compound,
            pharmacies = emptyList()
        )
        
        val html = CompoundView.render(data)
        
        // Check that all synonyms are rendered
        assertTrue(html.contains("Synonyms"))
        (1..10).forEach { i ->
            assertTrue(html.contains("Synonym $i"))
        }
    }
}