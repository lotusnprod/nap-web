package net.nprod.nap.pages.compoundCode

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.assertNotEquals
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import net.nprod.nap.types.CompoundCode
import net.nprod.nap.types.Compound

class CompoundCodeViewDataTest {
    
    @Test
    fun testCompoundCodeViewDataCreation() {
        val code = CompoundCode("https://nap.nprod.net/code/CAS123456", "CAS123456")
        val compound1 = Compound("https://nap.nprod.net/compound/1", number = 1)
        val compound2 = Compound("https://nap.nprod.net/compound/2", number = 2)
        val compounds = listOf(compound1, compound2)
        
        val viewData = CompoundCodeViewData(
            identifier = "CAS123456",
            compoundCode = code,
            compounds = compounds
        )
        
        assertEquals("CAS123456", viewData.identifier)
        assertEquals(code, viewData.compoundCode)
        assertEquals(compounds, viewData.compounds)
        assertEquals(2, viewData.compounds.size)
    }
    
    @Test
    fun testCompoundCodeViewDataWithEmptyCompounds() {
        val code = CompoundCode("https://nap.nprod.net/code/PubChem789", "PubChem789")
        val viewData = CompoundCodeViewData(
            identifier = "PubChem789",
            compoundCode = code,
            compounds = emptyList()
        )
        
        assertEquals("PubChem789", viewData.identifier)
        assertEquals(code, viewData.compoundCode)
        assertTrue(viewData.compounds.isEmpty())
    }
    
    @Test
    fun testCompoundCodeViewDataEquality() {
        val code1 = CompoundCode("https://nap.nprod.net/code/ChemSpider111", "ChemSpider111")
        val code2 = CompoundCode("https://nap.nprod.net/code/ChemSpider111", "ChemSpider111")
        val compound = Compound("https://nap.nprod.net/compound/100", number = 100)
        
        val viewData1 = CompoundCodeViewData("ChemSpider111", code1, listOf(compound))
        val viewData2 = CompoundCodeViewData("ChemSpider111", code2, listOf(compound))
        val viewData3 = CompoundCodeViewData("SMILES123", code1, listOf(compound))
        
        assertEquals(viewData1, viewData2)
        assertNotEquals(viewData1, viewData3)
    }
    
    @Test
    fun testCompoundCodeViewDataCopy() {
        val code = CompoundCode("https://nap.nprod.net/code/InChI999", "InChI999")
        val compound = Compound("https://nap.nprod.net/compound/200", number = 200)
        val viewData = CompoundCodeViewData("InChI999", code, listOf(compound))
        
        val newCompound = Compound("https://nap.nprod.net/compound/201", number = 201)
        val copy = viewData.copy(compounds = listOf(compound, newCompound))
        
        assertEquals(viewData.identifier, copy.identifier)
        assertEquals(viewData.compoundCode, copy.compoundCode)
        assertEquals(2, copy.compounds.size)
        assertNotEquals(viewData.compounds, copy.compounds)
    }
    
    @Test
    fun testCompoundCodeViewDataToString() {
        val code = CompoundCode("https://nap.nprod.net/code/KEGG555", "KEGG555")
        val compound = Compound("https://nap.nprod.net/compound/300", number = 300)
        val viewData = CompoundCodeViewData("KEGG555", code, listOf(compound))
        
        val string = viewData.toString()
        assertTrue(string.contains("CompoundCodeViewData"))
        assertTrue(string.contains("identifier=KEGG555"))
        assertTrue(string.contains("compoundCode="))
        assertTrue(string.contains("compounds="))
    }
    
    @Test
    fun testCompoundCodeViewDataSerialization() {
        val code = CompoundCode("https://nap.nprod.net/code/HMDB777", "HMDB777")
        val compound = Compound("https://nap.nprod.net/compound/400", number = 400)
        val viewData = CompoundCodeViewData("HMDB777", code, listOf(compound))
        
        // Test serialization
        val json = Json.encodeToString(viewData)
        assertTrue(json.contains("\"identifier\":\"HMDB777\""))
        assertTrue(json.contains("\"compoundCode\""))
        assertTrue(json.contains("\"compounds\""))
        
        // Test deserialization
        val decoded = Json.decodeFromString<CompoundCodeViewData>(json)
        assertEquals(viewData.identifier, decoded.identifier)
        assertEquals(viewData.compoundCode.uri, decoded.compoundCode.uri)
        assertEquals(viewData.compoundCode.name, decoded.compoundCode.name)
        assertEquals(viewData.compounds.size, decoded.compounds.size)
        assertEquals(viewData.compounds[0].uri, decoded.compounds[0].uri)
        assertEquals(viewData.compounds[0].number, decoded.compounds[0].number)
    }
    
    @Test
    fun testCompoundCodeViewDataHashCode() {
        val code = CompoundCode("https://nap.nprod.net/code/DrugBank888", "DrugBank888")
        val compound = Compound("https://nap.nprod.net/compound/500", number = 500)
        
        val viewData1 = CompoundCodeViewData("DrugBank888", code, listOf(compound))
        val viewData2 = CompoundCodeViewData("DrugBank888", code, listOf(compound))
        
        assertEquals(viewData1.hashCode(), viewData2.hashCode())
    }
    
    @Test
    fun testCompoundCodeViewDataWithMultipleCompounds() {
        val code = CompoundCode("https://nap.nprod.net/code/MULTI", "MULTI")
        val compounds = (1..5).map { Compound("https://nap.nprod.net/compound/$it", number = it) }
        
        val viewData = CompoundCodeViewData("MULTI", code, compounds)
        
        assertEquals(5, viewData.compounds.size)
        compounds.forEachIndexed { index, compound ->
            assertEquals(compound, viewData.compounds[index])
            assertEquals(index + 1, viewData.compounds[index].number)
        }
    }
}