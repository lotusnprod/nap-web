package net.nprod.nap.pages.pharmacology

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.assertNotEquals
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import net.nprod.nap.types.Pharmacology
import net.nprod.nap.types.Pharmacy

class PharmacologyViewDataTest {
    
    @Test
    fun testPharmacologyViewDataCreation() {
        val pharmacology = Pharmacology("https://nap.nprod.net/pharmacology/antimicrobial", "Antimicrobial")
        val pharmacy1 = Pharmacy("https://nap.nprod.net/pharmacy/1")
        val pharmacy2 = Pharmacy("https://nap.nprod.net/pharmacy/2")
        val pharmacyResults = listOf(pharmacy1, pharmacy2)
        
        val viewData = PharmacologyViewData(
            identifier = "antimicrobial",
            pharmacology = pharmacology,
            pharmacyResults = pharmacyResults
        )
        
        assertEquals("antimicrobial", viewData.identifier)
        assertEquals(pharmacology, viewData.pharmacology)
        assertEquals(pharmacyResults, viewData.pharmacyResults)
        assertEquals(2, viewData.pharmacyResults.size)
    }
    
    @Test
    fun testPharmacologyViewDataWithEmptyPharmacyResults() {
        val pharmacology = Pharmacology("https://nap.nprod.net/pharmacology/antiviral", "Antiviral")
        val viewData = PharmacologyViewData(
            identifier = "antiviral",
            pharmacology = pharmacology,
            pharmacyResults = emptyList()
        )
        
        assertEquals("antiviral", viewData.identifier)
        assertEquals(pharmacology, viewData.pharmacology)
        assertTrue(viewData.pharmacyResults.isEmpty())
    }
    
    @Test
    fun testPharmacologyViewDataEquality() {
        val pharm1 = Pharmacology("https://nap.nprod.net/pharmacology/anticancer", "Anticancer")
        val pharm2 = Pharmacology("https://nap.nprod.net/pharmacology/anticancer", "Anticancer")
        val pharmacy = Pharmacy("https://nap.nprod.net/pharmacy/100")
        
        val viewData1 = PharmacologyViewData("anticancer", pharm1, listOf(pharmacy))
        val viewData2 = PharmacologyViewData("anticancer", pharm2, listOf(pharmacy))
        val viewData3 = PharmacologyViewData("antifungal", pharm1, listOf(pharmacy))
        
        assertEquals(viewData1, viewData2)
        assertNotEquals(viewData1, viewData3)
    }
    
    @Test
    fun testPharmacologyViewDataCopy() {
        val pharmacology = Pharmacology("https://nap.nprod.net/pharmacology/antiinflammatory", "Anti-inflammatory")
        val pharmacy = Pharmacy("https://nap.nprod.net/pharmacy/200")
        val viewData = PharmacologyViewData("antiinflammatory", pharmacology, listOf(pharmacy))
        
        val newPharmacy = Pharmacy("https://nap.nprod.net/pharmacy/201")
        val copy = viewData.copy(pharmacyResults = listOf(pharmacy, newPharmacy))
        
        assertEquals(viewData.identifier, copy.identifier)
        assertEquals(viewData.pharmacology, copy.pharmacology)
        assertEquals(2, copy.pharmacyResults.size)
        assertNotEquals(viewData.pharmacyResults, copy.pharmacyResults)
    }
    
    @Test
    fun testPharmacologyViewDataToString() {
        val pharmacology = Pharmacology("https://nap.nprod.net/pharmacology/antioxidant", "Antioxidant")
        val pharmacy = Pharmacy("https://nap.nprod.net/pharmacy/300")
        val viewData = PharmacologyViewData("antioxidant", pharmacology, listOf(pharmacy))
        
        val string = viewData.toString()
        assertTrue(string.contains("PharmacologyViewData"))
        assertTrue(string.contains("identifier=antioxidant"))
        assertTrue(string.contains("pharmacology="))
        assertTrue(string.contains("pharmacyResults="))
    }
    
    @Test
    fun testPharmacologyViewDataSerialization() {
        val pharmacology = Pharmacology("https://nap.nprod.net/pharmacology/immunomodulatory", "Immunomodulatory")
        val pharmacy = Pharmacy("https://nap.nprod.net/pharmacy/400")
        val viewData = PharmacologyViewData("immunomodulatory", pharmacology, listOf(pharmacy))
        
        // Test serialization
        val json = Json.encodeToString(viewData)
        assertTrue(json.contains("\"identifier\":\"immunomodulatory\""))
        assertTrue(json.contains("\"pharmacology\""))
        assertTrue(json.contains("\"pharmacyResults\""))
        
        // Test deserialization
        val decoded = Json.decodeFromString<PharmacologyViewData>(json)
        assertEquals(viewData.identifier, decoded.identifier)
        assertEquals(viewData.pharmacology.uri, decoded.pharmacology.uri)
        assertEquals(viewData.pharmacyResults.size, decoded.pharmacyResults.size)
        assertEquals(viewData.pharmacyResults[0].uri, decoded.pharmacyResults[0].uri)
    }
    
    @Test
    fun testPharmacologyViewDataHashCode() {
        val pharmacology = Pharmacology("https://nap.nprod.net/pharmacology/neuroprotective", "Neuroprotective")
        val pharmacy = Pharmacy("https://nap.nprod.net/pharmacy/500")
        
        val viewData1 = PharmacologyViewData("neuroprotective", pharmacology, listOf(pharmacy))
        val viewData2 = PharmacologyViewData("neuroprotective", pharmacology, listOf(pharmacy))
        
        assertEquals(viewData1.hashCode(), viewData2.hashCode())
    }
    
    @Test
    fun testPharmacologyViewDataWithMultiplePharmacyResults() {
        val pharmacology = Pharmacology("https://nap.nprod.net/pharmacology/hepatoprotective", "Hepatoprotective")
        val pharmacyResults = (1..5).map { Pharmacy("https://nap.nprod.net/pharmacy/$it") }
        
        val viewData = PharmacologyViewData("hepatoprotective", pharmacology, pharmacyResults)
        
        assertEquals(5, viewData.pharmacyResults.size)
        pharmacyResults.forEachIndexed { index, pharmacy ->
            assertEquals(pharmacy, viewData.pharmacyResults[index])
        }
    }
}