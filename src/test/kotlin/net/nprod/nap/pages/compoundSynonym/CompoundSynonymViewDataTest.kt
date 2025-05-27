package net.nprod.nap.pages.compoundSynonym

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.assertNotEquals
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import net.nprod.nap.types.CompoundSynonym
import net.nprod.nap.types.Compound

class CompoundSynonymViewDataTest {
    
    @Test
    fun testCompoundSynonymViewDataCreation() {
        val synonym = CompoundSynonym("https://nap.nprod.net/synonym/aspirin", "Aspirin")
        val compound1 = Compound("https://nap.nprod.net/compound/1", number = 1)
        val compound2 = Compound("https://nap.nprod.net/compound/2", number = 2)
        val compounds = listOf(compound1, compound2)
        
        val viewData = CompoundSynonymViewData(
            identifier = "aspirin",
            synonym = synonym,
            compounds = compounds
        )
        
        assertEquals("aspirin", viewData.identifier)
        assertEquals(synonym, viewData.synonym)
        assertEquals(compounds, viewData.compounds)
        assertEquals(2, viewData.compounds.size)
    }
    
    @Test
    fun testCompoundSynonymViewDataWithEmptyCompounds() {
        val synonym = CompoundSynonym("https://nap.nprod.net/synonym/unknown", "Unknown")
        val viewData = CompoundSynonymViewData(
            identifier = "unknown",
            synonym = synonym,
            compounds = emptyList()
        )
        
        assertEquals("unknown", viewData.identifier)
        assertEquals(synonym, viewData.synonym)
        assertTrue(viewData.compounds.isEmpty())
    }
    
    @Test
    fun testCompoundSynonymViewDataEquality() {
        val synonym1 = CompoundSynonym("https://nap.nprod.net/synonym/paracetamol", "Paracetamol")
        val synonym2 = CompoundSynonym("https://nap.nprod.net/synonym/paracetamol", "Paracetamol")
        val compound = Compound("https://nap.nprod.net/compound/100", number = 100)
        
        val viewData1 = CompoundSynonymViewData("paracetamol", synonym1, listOf(compound))
        val viewData2 = CompoundSynonymViewData("paracetamol", synonym2, listOf(compound))
        val viewData3 = CompoundSynonymViewData("acetaminophen", synonym1, listOf(compound))
        
        assertEquals(viewData1, viewData2)
        assertNotEquals(viewData1, viewData3)
    }
    
    @Test
    fun testCompoundSynonymViewDataCopy() {
        val synonym = CompoundSynonym("https://nap.nprod.net/synonym/ibuprofen", "Ibuprofen")
        val compound = Compound("https://nap.nprod.net/compound/200", number = 200)
        val viewData = CompoundSynonymViewData("ibuprofen", synonym, listOf(compound))
        
        val newCompound = Compound("https://nap.nprod.net/compound/201", number = 201)
        val copy = viewData.copy(compounds = listOf(compound, newCompound))
        
        assertEquals(viewData.identifier, copy.identifier)
        assertEquals(viewData.synonym, copy.synonym)
        assertEquals(2, copy.compounds.size)
        assertNotEquals(viewData.compounds, copy.compounds)
    }
    
    @Test
    fun testCompoundSynonymViewDataToString() {
        val synonym = CompoundSynonym("https://nap.nprod.net/synonym/morphine", "Morphine")
        val compound = Compound("https://nap.nprod.net/compound/300", number = 300)
        val viewData = CompoundSynonymViewData("morphine", synonym, listOf(compound))
        
        val string = viewData.toString()
        assertTrue(string.contains("CompoundSynonymViewData"))
        assertTrue(string.contains("identifier=morphine"))
        assertTrue(string.contains("synonym="))
        assertTrue(string.contains("compounds="))
    }
    
    @Test
    fun testCompoundSynonymViewDataSerialization() {
        val synonym = CompoundSynonym("https://nap.nprod.net/synonym/caffeine", "Caffeine")
        val compound = Compound("https://nap.nprod.net/compound/400", number = 400)
        val viewData = CompoundSynonymViewData("caffeine", synonym, listOf(compound))
        
        // Test serialization
        val json = Json.encodeToString(viewData)
        assertTrue(json.contains("\"identifier\":\"caffeine\""))
        assertTrue(json.contains("\"synonym\""))
        assertTrue(json.contains("\"compounds\""))
        
        // Test deserialization
        val decoded = Json.decodeFromString<CompoundSynonymViewData>(json)
        assertEquals(viewData.identifier, decoded.identifier)
        assertEquals(viewData.synonym.uri, decoded.synonym.uri)
        assertEquals(viewData.synonym.name, decoded.synonym.name)
        assertEquals(viewData.compounds.size, decoded.compounds.size)
        assertEquals(viewData.compounds[0].uri, decoded.compounds[0].uri)
        assertEquals(viewData.compounds[0].number, decoded.compounds[0].number)
    }
    
    @Test
    fun testCompoundSynonymViewDataHashCode() {
        val synonym = CompoundSynonym("https://nap.nprod.net/synonym/nicotine", "Nicotine")
        val compound = Compound("https://nap.nprod.net/compound/500", number = 500)
        
        val viewData1 = CompoundSynonymViewData("nicotine", synonym, listOf(compound))
        val viewData2 = CompoundSynonymViewData("nicotine", synonym, listOf(compound))
        
        assertEquals(viewData1.hashCode(), viewData2.hashCode())
    }
    
    @Test
    fun testCompoundSynonymViewDataWithMultipleCompounds() {
        val synonym = CompoundSynonym("https://nap.nprod.net/synonym/generic", "Generic Name")
        val compounds = (1..5).map { Compound("https://nap.nprod.net/compound/$it", number = it) }
        
        val viewData = CompoundSynonymViewData("generic", synonym, compounds)
        
        assertEquals(5, viewData.compounds.size)
        compounds.forEachIndexed { index, compound ->
            assertEquals(compound, viewData.compounds[index])
            assertEquals(index + 1, viewData.compounds[index].number)
        }
    }
}