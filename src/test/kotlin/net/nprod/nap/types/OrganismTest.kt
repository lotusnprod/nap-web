package net.nprod.nap.types

import kotlin.test.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString

class OrganismTest {
    
    @Test
    fun testOrganismCreation() {
        val organism = Organism("https://nap.nprod.net/organism/1")
        
        assertEquals("https://nap.nprod.net/organism/1", organism.uri)
        assertNull(organism.familyname)
        assertNull(organism.genusname)
        assertNull(organism.speciesname)
        assertNull(organism.taxon)
        assertNull(organism.taxonName)
        assertNull(organism.collectedPart)
        assertNull(organism.citation)
        assertNull(organism.organismClass)
        assertNull(organism.speciesAuthority)
        assertNull(organism.subSpeciesAuthority)
        assertNull(organism.subSpeciesName)
        assertNull(organism.geographicalArea)
        assertNull(organism.country)
        assertNull(organism.comment)
        assertNull(organism.partCondition)
    }
    
    @Test
    fun testOrganismWithAllFields() {
        val citation = Citation("https://nap.nprod.net/citation/1")
        val collectedPart = CollectedPart("https://nap.nprod.net/part/leaf", "Leaf")
        val organismClass = OrganismClass("https://nap.nprod.net/class/fungi", "Fungi")
        val geographicalArea = GeographicalArea("https://nap.nprod.net/area/amazon", "Amazon")
        val country = Country("https://nap.nprod.net/country/brazil", "Brazil")
        val partCondition = PartCondition("https://nap.nprod.net/condition/fresh", "Fresh")
        
        val organism = Organism("https://nap.nprod.net/organism/2").apply {
            familyname = "Aspergillaceae"
            genusname = "Aspergillus"
            speciesname = "niger"
            taxon = "https://nap.nprod.net/taxon/5061"
            taxonName = "Aspergillus niger"
            this.collectedPart = collectedPart
            this.citation = citation
            this.organismClass = organismClass
            speciesAuthority = "Tiegh."
            subSpeciesAuthority = null
            subSpeciesName = null
            this.geographicalArea = geographicalArea
            this.country = country
            comment = "Collected in 2023"
            this.partCondition = partCondition
        }
        
        assertEquals("https://nap.nprod.net/organism/2", organism.uri)
        assertEquals("Aspergillaceae", organism.familyname)
        assertEquals("Aspergillus", organism.genusname)
        assertEquals("niger", organism.speciesname)
        assertEquals("https://nap.nprod.net/taxon/5061", organism.taxon)
        assertEquals("Aspergillus niger", organism.taxonName)
        assertEquals(collectedPart, organism.collectedPart)
        assertEquals(citation, organism.citation)
        assertEquals(organismClass, organism.organismClass)
        assertEquals("Tiegh.", organism.speciesAuthority)
        assertNull(organism.subSpeciesAuthority)
        assertNull(organism.subSpeciesName)
        assertEquals(geographicalArea, organism.geographicalArea)
        assertEquals(country, organism.country)
        assertEquals("Collected in 2023", organism.comment)
        assertEquals(partCondition, organism.partCondition)
    }
    
    @Test
    fun testNameForHumansWithGenusAndSpecies() {
        val organism = Organism("https://nap.nprod.net/organism/3").apply {
            genusname = "ESCHERICHIA"
            speciesname = "COLI"
        }
        
        assertEquals("Escherichia coli", organism.nameForHumans())
    }
    
    @Test
    fun testNameForHumansWithGenusSpeciesAndFamily() {
        val organism = Organism("https://nap.nprod.net/organism/4").apply {
            familyname = "Enterobacteriaceae"
            genusname = "Escherichia"
            speciesname = "coli"
        }
        
        assertEquals("Escherichia coli (Enterobacteriaceae)", organism.nameForHumans())
    }
    
    @Test
    fun testNameForHumansWithOnlyGenus() {
        val organism = Organism("https://nap.nprod.net/organism/5").apply {
            genusname = "Bacillus"
        }
        
        assertEquals("Bacillus", organism.nameForHumans())
    }
    
    @Test
    fun testNameForHumansWithOnlyFamily() {
        val organism = Organism("https://nap.nprod.net/organism/6").apply {
            familyname = "Fabaceae"
        }
        
        assertEquals("(Fabaceae)", organism.nameForHumans())
    }
    
    @Test
    fun testNameForHumansWithEmptyStrings() {
        val organism = Organism("https://nap.nprod.net/organism/7").apply {
            genusname = ""
            speciesname = ""
            familyname = ""
        }
        
        assertEquals("Empty organism", organism.nameForHumans())
    }
    
    @Test
    fun testNameForHumansWithNullValues() {
        val organism = Organism("https://nap.nprod.net/organism/8")
        // All fields are null by default
        
        assertEquals("Empty organism", organism.nameForHumans())
    }
    
    @Test
    fun testNameForHumansCaseNormalization() {
        val organism = Organism("https://nap.nprod.net/organism/9").apply {
            genusname = "aSPERGILLUS"
            speciesname = "FUMIGATUS"
            familyname = "trichocomaceae"
        }
        
        assertEquals("Aspergillus fumigatus (trichocomaceae)", organism.nameForHumans())
    }
    
    @Test
    fun testOrganismEquality() {
        val organism1 = Organism("https://nap.nprod.net/organism/10").apply {
            genusname = "Penicillium"
            speciesname = "chrysogenum"
        }
        
        val organism2 = Organism("https://nap.nprod.net/organism/10").apply {
            genusname = "Penicillium"
            speciesname = "chrysogenum"
        }
        
        val organism3 = Organism("https://nap.nprod.net/organism/11").apply {
            genusname = "Penicillium"
            speciesname = "chrysogenum"
        }
        
        assertEquals(organism1, organism2)
        assertNotEquals(organism1, organism3)
    }
    
    @Test
    fun testOrganismCopy() {
        val organism = Organism("https://nap.nprod.net/organism/12").apply {
            genusname = "Streptomyces"
            speciesname = "coelicolor"
            comment = "Original comment"
        }
        
        val copy = organism.copy(comment = "Modified comment")
        
        assertEquals(organism.uri, copy.uri)
        assertEquals(organism.genusname, copy.genusname)
        assertEquals(organism.speciesname, copy.speciesname)
        assertNotEquals(organism.comment, copy.comment)
        assertEquals("Modified comment", copy.comment)
    }
    
    @Test
    fun testOrganismToString() {
        val organism = Organism("https://nap.nprod.net/organism/13").apply {
            genusname = "Saccharomyces"
            speciesname = "cerevisiae"
        }
        
        val string = organism.toString()
        assertTrue(string.contains("Organism"))
        assertTrue(string.contains("uri=https://nap.nprod.net/organism/13"))
        assertTrue(string.contains("genusname=Saccharomyces"))
        assertTrue(string.contains("speciesname=cerevisiae"))
    }
    
    @Test
    fun testOrganismSerialization() {
        val organism = Organism("https://nap.nprod.net/organism/14").apply {
            familyname = "Agaricaceae"
            genusname = "Agaricus"
            speciesname = "bisporus"
            comment = "Common mushroom"
        }
        
        val json = Json.encodeToString(organism)
        assertTrue(json.contains("\"uri\":\"https://nap.nprod.net/organism/14\""))
        assertTrue(json.contains("\"familyname\":\"Agaricaceae\""))
        assertTrue(json.contains("\"genusname\":\"Agaricus\""))
        assertTrue(json.contains("\"speciesname\":\"bisporus\""))
        assertTrue(json.contains("\"comment\":\"Common mushroom\""))
        
        val decoded = Json.decodeFromString<Organism>(json)
        assertEquals(organism, decoded)
    }
    
    @Test
    fun testOrganismHashCode() {
        val organism1 = Organism("https://nap.nprod.net/organism/15").apply {
            genusname = "Lactobacillus"
            speciesname = "acidophilus"
        }
        
        val organism2 = Organism("https://nap.nprod.net/organism/15").apply {
            genusname = "Lactobacillus"
            speciesname = "acidophilus"
        }
        
        assertEquals(organism1.hashCode(), organism2.hashCode())
    }
    
    @Test
    fun testOrganismWithSubspecies() {
        val organism = Organism("https://nap.nprod.net/organism/16").apply {
            genusname = "Bacillus"
            speciesname = "subtilis"
            subSpeciesName = "spizizenii"
            subSpeciesAuthority = "Nakamura et al. 1999"
            taxon = "https://nap.nprod.net/taxon/parent"
            taxonName = "Bacillus subtilis"
        }
        
        assertEquals("Bacillus", organism.genusname)
        assertEquals("subtilis", organism.speciesname)
        assertEquals("spizizenii", organism.subSpeciesName)
        assertEquals("Nakamura et al. 1999", organism.subSpeciesAuthority)
    }
    
    @Test
    fun testNameForHumansWithMixedCase() {
        val organism = Organism("https://nap.nprod.net/organism/17").apply {
            genusname = "pEnIcIlLiUm"
            speciesname = "NoTaTuM"
            familyname = "TRICHOCOMACEAE"
        }
        
        assertEquals("Penicillium notatum (TRICHOCOMACEAE)", organism.nameForHumans())
    }
    
    @Test
    fun testOrganismWithPartialData() {
        val organism = Organism("https://nap.nprod.net/organism/18").apply {
            genusname = "Candida"
            // species is null
            familyname = "Saccharomycetaceae"
            country = Country("https://nap.nprod.net/country/usa", "USA")
        }
        
        assertEquals("Candida (Saccharomycetaceae)", organism.nameForHumans())
        assertNotNull(organism.country)
        assertEquals("USA", organism.country?.name)
    }
}