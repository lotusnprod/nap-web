package net.nprod.nap.pages.worktype

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.assertNotEquals
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import net.nprod.nap.types.Worktype
import net.nprod.nap.types.WorktypeGroup
import net.nprod.nap.types.Pharmacy

class WorktypeViewDataTest {
    
    @Test
    fun testWorktypeViewDataCreation() {
        val group = WorktypeGroup("https://nap.nprod.net/worktypegroup/analysis", "ANA", "Analysis")
        val worktype = Worktype("https://nap.nprod.net/worktype/hplc", "HPLC", "HPLC", group)
        val pharmacy1 = Pharmacy("https://nap.nprod.net/pharmacy/1")
        val pharmacy2 = Pharmacy("https://nap.nprod.net/pharmacy/2")
        val pharmacyResults = listOf(pharmacy1, pharmacy2)
        
        val viewData = WorktypeViewData(
            identifier = "hplc",
            worktype = worktype,
            pharmacyResults = pharmacyResults
        )
        
        assertEquals("hplc", viewData.identifier)
        assertEquals(worktype, viewData.worktype)
        assertEquals(pharmacyResults, viewData.pharmacyResults)
        assertEquals(2, viewData.pharmacyResults.size)
    }
    
    @Test
    fun testWorktypeViewDataWithEmptyPharmacyResults() {
        val worktype = Worktype("https://nap.nprod.net/worktype/nmr", "NMR", "NMR Spectroscopy")
        val viewData = WorktypeViewData(
            identifier = "nmr",
            worktype = worktype,
            pharmacyResults = emptyList()
        )
        
        assertEquals("nmr", viewData.identifier)
        assertEquals(worktype, viewData.worktype)
        assertTrue(viewData.pharmacyResults.isEmpty())
    }
    
    @Test
    fun testWorktypeViewDataEquality() {
        val worktype1 = Worktype("https://nap.nprod.net/worktype/ms", "MS", "Mass Spectrometry")
        val worktype2 = Worktype("https://nap.nprod.net/worktype/ms", "MS", "Mass Spectrometry")
        val pharmacy = Pharmacy("https://nap.nprod.net/pharmacy/100")
        
        val viewData1 = WorktypeViewData("ms", worktype1, listOf(pharmacy))
        val viewData2 = WorktypeViewData("ms", worktype2, listOf(pharmacy))
        val viewData3 = WorktypeViewData("gc", worktype1, listOf(pharmacy))
        
        assertEquals(viewData1, viewData2)
        assertNotEquals(viewData1, viewData3)
    }
    
    @Test
    fun testWorktypeViewDataCopy() {
        val worktype = Worktype("https://nap.nprod.net/worktype/tlc", "TLC", "Thin Layer Chromatography")
        val pharmacy = Pharmacy("https://nap.nprod.net/pharmacy/200")
        val viewData = WorktypeViewData("tlc", worktype, listOf(pharmacy))
        
        val newPharmacy = Pharmacy("https://nap.nprod.net/pharmacy/201")
        val copy = viewData.copy(pharmacyResults = listOf(pharmacy, newPharmacy))
        
        assertEquals(viewData.identifier, copy.identifier)
        assertEquals(viewData.worktype, copy.worktype)
        assertEquals(2, copy.pharmacyResults.size)
        assertNotEquals(viewData.pharmacyResults, copy.pharmacyResults)
    }
    
    @Test
    fun testWorktypeViewDataToString() {
        val worktype = Worktype("https://nap.nprod.net/worktype/ir", "IR", "Infrared Spectroscopy")
        val pharmacy = Pharmacy("https://nap.nprod.net/pharmacy/300")
        val viewData = WorktypeViewData("ir", worktype, listOf(pharmacy))
        
        val string = viewData.toString()
        assertTrue(string.contains("WorktypeViewData"))
        assertTrue(string.contains("identifier=ir"))
        assertTrue(string.contains("worktype="))
        assertTrue(string.contains("pharmacyResults="))
    }
    
    @Test
    fun testWorktypeViewDataSerialization() {
        val group = WorktypeGroup("https://nap.nprod.net/worktypegroup/separation", "SEP", "Separation")
        val worktype = Worktype("https://nap.nprod.net/worktype/cc", "CC", "Column Chromatography", group)
        val pharmacy = Pharmacy("https://nap.nprod.net/pharmacy/400")
        val viewData = WorktypeViewData("cc", worktype, listOf(pharmacy))
        
        // Test serialization
        val json = Json.encodeToString(viewData)
        assertTrue(json.contains("\"identifier\":\"cc\""))
        assertTrue(json.contains("\"worktype\""))
        assertTrue(json.contains("\"pharmacyResults\""))
        
        // Test deserialization
        val decoded = Json.decodeFromString<WorktypeViewData>(json)
        assertEquals(viewData.identifier, decoded.identifier)
        assertEquals(viewData.worktype.uri, decoded.worktype.uri)
        assertEquals(viewData.worktype.code, decoded.worktype.code)
        assertEquals(viewData.worktype.name, decoded.worktype.name)
        assertEquals(viewData.pharmacyResults.size, decoded.pharmacyResults.size)
        assertEquals(viewData.pharmacyResults[0].uri, decoded.pharmacyResults[0].uri)
    }
    
    @Test
    fun testWorktypeViewDataHashCode() {
        val worktype = Worktype("https://nap.nprod.net/worktype/prep", "PREP", "Preparative HPLC")
        val pharmacy = Pharmacy("https://nap.nprod.net/pharmacy/500")
        
        val viewData1 = WorktypeViewData("prep", worktype, listOf(pharmacy))
        val viewData2 = WorktypeViewData("prep", worktype, listOf(pharmacy))
        
        assertEquals(viewData1.hashCode(), viewData2.hashCode())
    }
    
    @Test
    fun testWorktypeViewDataWithMultiplePharmacyResults() {
        val worktype = Worktype("https://nap.nprod.net/worktype/bioassay", "BIO", "Bioassay")
        val pharmacyResults = (1..5).map { Pharmacy("https://nap.nprod.net/pharmacy/$it") }
        
        val viewData = WorktypeViewData("bioassay", worktype, pharmacyResults)
        
        assertEquals(5, viewData.pharmacyResults.size)
        pharmacyResults.forEachIndexed { index, pharmacy ->
            assertEquals(pharmacy, viewData.pharmacyResults[index])
        }
    }
}