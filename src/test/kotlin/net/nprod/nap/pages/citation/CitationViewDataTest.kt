package net.nprod.nap.pages.citation

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.assertNotEquals
import kotlin.test.assertNull
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import net.nprod.nap.types.Citation

class CitationViewDataTest {
    
    @Test
    fun testCitationViewDataCreation() {
        val citation = Citation("https://nap.nprod.net/citation/123")
        val organismData = listOf(
            OrganismData(
                uri = "https://nap.nprod.net/organism/1",
                genus = "Genus",
                species = "species",
                family = "Family",
                experiments = emptyList()
            )
        )
        
        val viewData = CitationViewData(
            identifier = "123",
            citation = citation,
            formattedCitation = "Test Citation (2023)",
            organismData = organismData
        )
        
        assertEquals("123", viewData.identifier)
        assertEquals(citation, viewData.citation)
        assertEquals("Test Citation (2023)", viewData.formattedCitation)
        assertEquals(organismData, viewData.organismData)
    }
    
    @Test
    fun testCitationViewDataSerialization() {
        val citation = Citation("https://nap.nprod.net/citation/456")
        val viewData = CitationViewData(
            identifier = "456",
            citation = citation,
            formattedCitation = "Another Citation",
            organismData = emptyList()
        )
        
        val json = Json.encodeToString(viewData)
        assertTrue(json.contains("\"identifier\":\"456\""))
        assertTrue(json.contains("\"formattedCitation\":\"Another Citation\""))
        
        val decoded = Json.decodeFromString<CitationViewData>(json)
        assertEquals(viewData, decoded)
    }
    
    @Test
    fun testOrganismDataCreation() {
        val experiments = listOf(
            ExperimentData(
                uri = "https://nap.nprod.net/experiment/1",
                number = "EXP001",
                pharmacologyName = "Test Pharmacology",
                worktypes = emptyList(),
                qualitativeResults = emptyList(),
                compounds = emptyList()
            )
        )
        
        val organismData = OrganismData(
            uri = "https://nap.nprod.net/organism/1",
            genus = "Homo",
            species = "sapiens",
            family = "Hominidae",
            experiments = experiments
        )
        
        assertEquals("https://nap.nprod.net/organism/1", organismData.uri)
        assertEquals("Homo", organismData.genus)
        assertEquals("sapiens", organismData.species)
        assertEquals("Hominidae", organismData.family)
        assertEquals(experiments, organismData.experiments)
    }
    
    @Test
    fun testOrganismDataWithNullFields() {
        val organismData = OrganismData(
            uri = "https://nap.nprod.net/organism/2",
            genus = null,
            species = null,
            family = null,
            experiments = emptyList()
        )
        
        assertNull(organismData.genus)
        assertNull(organismData.species)
        assertNull(organismData.family)
        assertTrue(organismData.experiments.isEmpty())
    }
    
    @Test
    fun testExperimentDataCreation() {
        val worktypes = listOf(WorktypeData("https://nap.nprod.net/worktype/1", "Isolation"))
        val qualResults = listOf(QualitativeResultData("https://nap.nprod.net/result/1", "Active"))
        val compounds = listOf(CompoundData("https://nap.nprod.net/compound/1", "Compound A"))
        
        val experimentData = ExperimentData(
            uri = "https://nap.nprod.net/experiment/1",
            number = "001",
            pharmacologyName = "Antimicrobial",
            worktypes = worktypes,
            qualitativeResults = qualResults,
            compounds = compounds
        )
        
        assertEquals("https://nap.nprod.net/experiment/1", experimentData.uri)
        assertEquals("001", experimentData.number)
        assertEquals("Antimicrobial", experimentData.pharmacologyName)
        assertEquals(worktypes, experimentData.worktypes)
        assertEquals(qualResults, experimentData.qualitativeResults)
        assertEquals(compounds, experimentData.compounds)
    }
    
    @Test
    fun testWorktypeDataCreation() {
        val worktype = WorktypeData(
            uri = "https://nap.nprod.net/worktype/1",
            name = "Extraction"
        )
        
        assertEquals("https://nap.nprod.net/worktype/1", worktype.uri)
        assertEquals("Extraction", worktype.name)
    }
    
    @Test
    fun testQualitativeResultDataCreation() {
        val result = QualitativeResultData(
            uri = "https://nap.nprod.net/result/1",
            name = "Positive"
        )
        
        assertEquals("https://nap.nprod.net/result/1", result.uri)
        assertEquals("Positive", result.name)
    }
    
    @Test
    fun testCompoundDataCreation() {
        val compound = CompoundData(
            uri = "https://nap.nprod.net/compound/1",
            name = "Aspirin"
        )
        
        assertEquals("https://nap.nprod.net/compound/1", compound.uri)
        assertEquals("Aspirin", compound.name)
    }
    
    @Test
    fun testComplexCitationViewData() {
        val citation = Citation("https://nap.nprod.net/citation/complex")
        
        val compounds = listOf(
            CompoundData("https://nap.nprod.net/compound/1", "Compound 1"),
            CompoundData("https://nap.nprod.net/compound/2", "Compound 2")
        )
        
        val worktypes = listOf(
            WorktypeData("https://nap.nprod.net/worktype/1", "Type 1"),
            WorktypeData("https://nap.nprod.net/worktype/2", "Type 2")
        )
        
        val qualResults = listOf(
            QualitativeResultData("https://nap.nprod.net/result/1", "Result 1")
        )
        
        val experiments = listOf(
            ExperimentData(
                uri = "https://nap.nprod.net/experiment/1",
                number = "001",
                pharmacologyName = "Pharm 1",
                worktypes = worktypes,
                qualitativeResults = qualResults,
                compounds = compounds
            )
        )
        
        val organismData = listOf(
            OrganismData(
                uri = "https://nap.nprod.net/organism/1",
                genus = "Genus1",
                species = "species1",
                family = "Family1",
                experiments = experiments
            )
        )
        
        val viewData = CitationViewData(
            identifier = "complex",
            citation = citation,
            formattedCitation = "Complex Citation",
            organismData = organismData
        )
        
        // Test serialization of complex nested structure
        val json = Json.encodeToString(viewData)
        val decoded = Json.decodeFromString<CitationViewData>(json)
        
        assertEquals(viewData.identifier, decoded.identifier)
        assertEquals(viewData.formattedCitation, decoded.formattedCitation)
        assertEquals(viewData.organismData.size, decoded.organismData.size)
        assertEquals(viewData.organismData[0].experiments.size, decoded.organismData[0].experiments.size)
        assertEquals(viewData.organismData[0].experiments[0].compounds.size, 
                     decoded.organismData[0].experiments[0].compounds.size)
    }
    
    @Test
    fun testDataClassEquality() {
        val worktype1 = WorktypeData("uri1", "name1")
        val worktype2 = WorktypeData("uri1", "name1")
        val worktype3 = WorktypeData("uri2", "name2")
        
        assertEquals(worktype1, worktype2)
        assertNotEquals(worktype1, worktype3)
        
        val compound1 = CompoundData("uri1", "name1")
        val compound2 = CompoundData("uri1", "name1")
        
        assertEquals(compound1, compound2)
        assertEquals(compound1.hashCode(), compound2.hashCode())
    }
    
    @Test
    fun testDataClassCopy() {
        val original = CompoundData("uri", "original")
        val copy = original.copy(name = "modified")
        
        assertEquals(original.uri, copy.uri)
        assertEquals("modified", copy.name)
        assertNotEquals(original, copy)
    }
    
    @Test
    fun testToString() {
        val worktype = WorktypeData("uri", "name")
        assertTrue(worktype.toString().contains("WorktypeData"))
        assertTrue(worktype.toString().contains("uri=uri"))
        assertTrue(worktype.toString().contains("name=name"))
        
        val result = QualitativeResultData("uri", "name")
        assertTrue(result.toString().contains("QualitativeResultData"))
        
        val compound = CompoundData("uri", "name")
        assertTrue(compound.toString().contains("CompoundData"))
    }
}