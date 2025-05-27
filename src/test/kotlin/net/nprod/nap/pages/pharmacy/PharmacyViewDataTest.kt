package net.nprod.nap.pages.pharmacy

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import net.nprod.nap.types.Pharmacy

class PharmacyViewDataTest {
    
    @Test
    fun testPharmacyViewDataCreation() {
        val pharmacy = Pharmacy("https://nap.nprod.net/pharmacy/123")
        val viewData = PharmacyViewData(
            identifier = "123",
            pharmacy = pharmacy
        )
        
        assertEquals("123", viewData.identifier)
        assertEquals(pharmacy, viewData.pharmacy)
    }
    
    @Test
    fun testPharmacyViewDataSerialization() {
        val pharmacy = Pharmacy("https://nap.nprod.net/pharmacy/456")
        val viewData = PharmacyViewData(
            identifier = "456",
            pharmacy = pharmacy
        )
        
        // Test serialization
        val json = Json.encodeToString(viewData)
        assertTrue(json.contains("\"identifier\":\"456\""))
        assertTrue(json.contains("\"pharmacy\""))
        assertTrue(json.contains("https://nap.nprod.net/pharmacy/456"))
        
        // Test deserialization
        val decoded = Json.decodeFromString<PharmacyViewData>(json)
        assertEquals(viewData.identifier, decoded.identifier)
        assertEquals(viewData.pharmacy.uri, decoded.pharmacy.uri)
    }
    
    @Test
    fun testPharmacyViewDataEquality() {
        val pharmacy1 = Pharmacy("https://nap.nprod.net/pharmacy/789")
        val pharmacy2 = Pharmacy("https://nap.nprod.net/pharmacy/789")
        
        val viewData1 = PharmacyViewData("789", pharmacy1)
        val viewData2 = PharmacyViewData("789", pharmacy2)
        
        assertEquals(viewData1, viewData2)
    }
    
    @Test
    fun testPharmacyViewDataCopy() {
        val pharmacy = Pharmacy("https://nap.nprod.net/pharmacy/999")
        val viewData = PharmacyViewData("999", pharmacy)
        
        val copy = viewData.copy(identifier = "1000")
        
        assertEquals("1000", copy.identifier)
        assertEquals(viewData.pharmacy, copy.pharmacy)
    }
    
    @Test
    fun testPharmacyViewDataToString() {
        val pharmacy = Pharmacy("https://nap.nprod.net/pharmacy/111")
        val viewData = PharmacyViewData("111", pharmacy)
        
        val string = viewData.toString()
        assertTrue(string.contains("PharmacyViewData"))
        assertTrue(string.contains("identifier=111"))
        assertTrue(string.contains("pharmacy="))
    }
}