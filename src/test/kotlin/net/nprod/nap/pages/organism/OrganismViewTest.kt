package net.nprod.nap.pages.organism

import kotlin.test.Test
import kotlin.test.assertTrue
import net.nprod.nap.types.*

class OrganismViewTest {
    
    @Test
    fun testRenderWithCompleteOrganism() {
        val organism = Organism("https://nap.nprod.net/organism/123").apply {
            taxon = "https://nap.nprod.net/taxon/1"
            familyname = "Rosaceae"
            genusname = "Rosa"
            speciesname = "canina"
            speciesAuthority = "L."
            subSpeciesName = "corymbifera"
            subSpeciesAuthority = "(Borkh.) Asch."
            comment = "Test comment"
            organismClass = OrganismClass("https://nap.nprod.net/organismclass/1", "Plant")
            collectedPart = CollectedPart("https://nap.nprod.net/part/1", "Leaves")
            partCondition = PartCondition("https://nap.nprod.net/condition/1", "Fresh")
            citation = Citation("https://nap.nprod.net/citation/1").apply {
                year = "2023"
                title = "Test Citation"
                authors = "Author A, Author B"
            }
            geographicalArea = GeographicalArea("https://nap.nprod.net/area/1", "Europe")
            country = Country("https://nap.nprod.net/country/1", "France")
        }
        
        val pharmacyResults = listOf(
            Pharmacy("https://nap.nprod.net/pharmacy/1"),
            Pharmacy("https://nap.nprod.net/pharmacy/2")
        )
        
        val data = OrganismViewData(
            identifier = "123",
            organism = organism,
            pharmacyResults = pharmacyResults
        )
        
        val html = OrganismView.render(data)
        
        // Check title
        assertTrue(html.contains("Organism 123 - Rosa canina (Rosaceae)"))
        
        // Check all fields are rendered
        assertTrue(html.contains("Organism Details"))
        assertTrue(html.contains("taxon/1"))
        assertTrue(html.contains("Rosaceae"))
        assertTrue(html.contains("Rosa"))
        assertTrue(html.contains("canina"))
        assertTrue(html.contains("L."))
        assertTrue(html.contains("corymbifera"))
        assertTrue(html.contains("(Borkh.) Asch."))
        assertTrue(html.contains("Test comment"))
        assertTrue(html.contains("Plant"))
        assertTrue(html.contains("Leaves"))
        assertTrue(html.contains("Fresh"))
        assertTrue(html.contains("Author A, Author B"))
        assertTrue(html.contains("Europe"))
        assertTrue(html.contains("France"))
        assertTrue(html.contains("Search for all experiments on this taxon"))
        assertTrue(html.contains("Experiments"))
    }
    
    @Test
    fun testRenderWithMinimalOrganism() {
        val organism = Organism("https://nap.nprod.net/organism/456")
        // No fields set
        
        val data = OrganismViewData(
            identifier = "456",
            organism = organism,
            pharmacyResults = emptyList()
        )
        
        val html = OrganismView.render(data)
        
        // Check basic rendering
        assertTrue(html.contains("Organism 456"))
        assertTrue(html.contains("Organism Details"))
        assertTrue(html.contains("Experiments"))
        
        // Should not contain optional fields
        assertTrue(!html.contains("Taxon"))
        assertTrue(!html.contains("Family"))
        assertTrue(!html.contains("Genus"))
        assertTrue(!html.contains("Species"))
        assertTrue(!html.contains("Search for all experiments on this taxon"))
    }
    
    @Test
    fun testRenderWithEmptyFamilyName() {
        val organism = Organism("https://nap.nprod.net/organism/789").apply {
            familyname = ""
            genusname = "Test"
            speciesname = "species"
        }
        
        val data = OrganismViewData(
            identifier = "789",
            organism = organism,
            pharmacyResults = emptyList()
        )
        
        val html = OrganismView.render(data)
        
        // Should not include empty parentheses for empty family name
        assertTrue(html.contains("Organism 789 - Test species"))
        assertTrue(!html.contains("()"))
    }
    
    @Test
    fun testRenderWithPartialFields() {
        val organism = Organism("https://nap.nprod.net/organism/999").apply {
            genusname = "Quercus"
            speciesname = "robur"
            // familyname is null
            taxon = "https://nap.nprod.net/taxon/2"
            // No other fields
        }
        
        val data = OrganismViewData(
            identifier = "999",
            organism = organism,
            pharmacyResults = listOf(Pharmacy("https://nap.nprod.net/pharmacy/3"))
        )
        
        val html = OrganismView.render(data)
        
        // Check title without family
        assertTrue(html.contains("Organism 999 - Quercus robur"))
        
        // Check taxon button is present
        assertTrue(html.contains("Search for all experiments on this taxon"))
        
        // Check only set fields are rendered
        assertTrue(html.contains("Quercus"))
        assertTrue(html.contains("robur"))
        assertTrue(html.contains("taxon/2"))
        
        // Should not contain null fields
        assertTrue(!html.contains("Species authority"))
        assertTrue(!html.contains("Comment"))
        assertTrue(!html.contains("Class"))
    }
    
    @Test
    fun testRenderWithNullGenusAndSpecies() {
        val organism = Organism("https://nap.nprod.net/organism/1000").apply {
            familyname = "Asteraceae"
            // genusname and speciesname are null
        }
        
        val data = OrganismViewData(
            identifier = "1000",
            organism = organism,
            pharmacyResults = emptyList()
        )
        
        val html = OrganismView.render(data)
        
        // Should handle null genus/species gracefully
        assertTrue(html.contains("Organism 1000"))
        assertTrue(html.contains("(Asteraceae)"))
    }
    
    @Test
    fun testRenderWithAllOptionalFields() {
        val organism = Organism("https://nap.nprod.net/organism/2000").apply {
            speciesAuthority = "Authority"
            subSpeciesName = "subspecies"
            subSpeciesAuthority = "SubAuth"
            comment = "Comment text"
            organismClass = OrganismClass("https://nap.nprod.net/class/1", "Fungi")
            collectedPart = CollectedPart("https://nap.nprod.net/part/2", "Roots")
            partCondition = PartCondition("https://nap.nprod.net/condition/2", "Dried")
        }
        
        val data = OrganismViewData(
            identifier = "2000",
            organism = organism,
            pharmacyResults = emptyList()
        )
        
        val html = OrganismView.render(data)
        
        // Check all optional fields are rendered
        assertTrue(html.contains("Species authority"))
        assertTrue(html.contains("Authority"))
        assertTrue(html.contains("Subspecies"))
        assertTrue(html.contains("subspecies"))
        assertTrue(html.contains("Subspecies authority"))
        assertTrue(html.contains("SubAuth"))
        assertTrue(html.contains("Comment"))
        assertTrue(html.contains("Comment text"))
        assertTrue(html.contains("Fungi"))
        assertTrue(html.contains("Roots"))
        assertTrue(html.contains("Dried"))
    }
}