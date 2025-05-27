package net.nprod.nap.pages.extract

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.assertNotEquals
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import net.nprod.nap.types.Extract
import net.nprod.nap.types.Pharmacy

class ExtractViewDataTest {
    
    @Test
    fun testExtractViewDataCreation() {
        val extract = Extract("https://nap.nprod.net/extract/aqueous", "AQ", "Aqueous Extract")
        val pharmacy1 = Pharmacy("https://nap.nprod.net/pharmacy/1")
        val pharmacy2 = Pharmacy("https://nap.nprod.net/pharmacy/2")
        val pharmacyResults = listOf(pharmacy1, pharmacy2)
        
        val viewData = ExtractViewData(
            identifier = "aqueous",
            extract = extract,
            pharmacyResults = pharmacyResults
        )
        
        assertEquals("aqueous", viewData.identifier)
        assertEquals(extract, viewData.extract)
        assertEquals(pharmacyResults, viewData.pharmacyResults)
        assertEquals(2, viewData.pharmacyResults.size)
    }
    
    @Test
    fun testExtractViewDataWithEmptyPharmacyResults() {
        val extract = Extract("https://nap.nprod.net/extract/ethanolic", "ETH", "Ethanolic Extract")
        val viewData = ExtractViewData(
            identifier = "ethanolic",
            extract = extract,
            pharmacyResults = emptyList()
        )
        
        assertEquals("ethanolic", viewData.identifier)
        assertEquals(extract, viewData.extract)
        assertTrue(viewData.pharmacyResults.isEmpty())
    }
    
    @Test
    fun testExtractViewDataEquality() {
        val extract1 = Extract("https://nap.nprod.net/extract/methanolic", "METH", "Methanolic Extract")
        val extract2 = Extract("https://nap.nprod.net/extract/methanolic", "METH", "Methanolic Extract")
        val pharmacy = Pharmacy("https://nap.nprod.net/pharmacy/100")
        
        val viewData1 = ExtractViewData("methanolic", extract1, listOf(pharmacy))
        val viewData2 = ExtractViewData("methanolic", extract2, listOf(pharmacy))
        val viewData3 = ExtractViewData("chloroform", extract1, listOf(pharmacy))
        
        assertEquals(viewData1, viewData2)
        assertNotEquals(viewData1, viewData3)
    }
    
    @Test
    fun testExtractViewDataCopy() {
        val extract = Extract("https://nap.nprod.net/extract/hexane", "HEX", "Hexane Extract")
        val pharmacy = Pharmacy("https://nap.nprod.net/pharmacy/200")
        val viewData = ExtractViewData("hexane", extract, listOf(pharmacy))
        
        val newPharmacy = Pharmacy("https://nap.nprod.net/pharmacy/201")
        val copy = viewData.copy(pharmacyResults = listOf(pharmacy, newPharmacy))
        
        assertEquals(viewData.identifier, copy.identifier)
        assertEquals(viewData.extract, copy.extract)
        assertEquals(2, copy.pharmacyResults.size)
        assertNotEquals(viewData.pharmacyResults, copy.pharmacyResults)
    }
    
    @Test
    fun testExtractViewDataToString() {
        val extract = Extract("https://nap.nprod.net/extract/acetone", "ACE", "Acetone Extract")
        val pharmacy = Pharmacy("https://nap.nprod.net/pharmacy/300")
        val viewData = ExtractViewData("acetone", extract, listOf(pharmacy))
        
        val string = viewData.toString()
        assertTrue(string.contains("ExtractViewData"))
        assertTrue(string.contains("identifier=acetone"))
        assertTrue(string.contains("extract="))
        assertTrue(string.contains("pharmacyResults="))
    }
    
    @Test
    fun testExtractViewDataSerialization() {
        val extract = Extract("https://nap.nprod.net/extract/dichloromethane", "DCM", "Dichloromethane Extract")
        val pharmacy = Pharmacy("https://nap.nprod.net/pharmacy/400")
        val viewData = ExtractViewData("dichloromethane", extract, listOf(pharmacy))
        
        // Test serialization
        val json = Json.encodeToString(viewData)
        assertTrue(json.contains("\"identifier\":\"dichloromethane\""))
        assertTrue(json.contains("\"extract\""))
        assertTrue(json.contains("\"pharmacyResults\""))
        
        // Test deserialization
        val decoded = Json.decodeFromString<ExtractViewData>(json)
        assertEquals(viewData.identifier, decoded.identifier)
        assertEquals(viewData.extract.uri, decoded.extract.uri)
        assertEquals(viewData.extract.code, decoded.extract.code)
        assertEquals(viewData.extract.name, decoded.extract.name)
        assertEquals(viewData.pharmacyResults.size, decoded.pharmacyResults.size)
        assertEquals(viewData.pharmacyResults[0].uri, decoded.pharmacyResults[0].uri)
    }
    
    @Test
    fun testExtractViewDataHashCode() {
        val extract = Extract("https://nap.nprod.net/extract/ethylacetate", "EtOAc", "Ethyl Acetate Extract")
        val pharmacy = Pharmacy("https://nap.nprod.net/pharmacy/500")
        
        val viewData1 = ExtractViewData("ethylacetate", extract, listOf(pharmacy))
        val viewData2 = ExtractViewData("ethylacetate", extract, listOf(pharmacy))
        
        assertEquals(viewData1.hashCode(), viewData2.hashCode())
    }
    
    @Test
    fun testExtractViewDataWithMultiplePharmacyResults() {
        val extract = Extract("https://nap.nprod.net/extract/butanol", "BuOH", "Butanol Extract")
        val pharmacyResults = (1..5).map { Pharmacy("https://nap.nprod.net/pharmacy/$it") }
        
        val viewData = ExtractViewData("butanol", extract, pharmacyResults)
        
        assertEquals(5, viewData.pharmacyResults.size)
        pharmacyResults.forEachIndexed { index, pharmacy ->
            assertEquals(pharmacy, viewData.pharmacyResults[index])
        }
    }
}