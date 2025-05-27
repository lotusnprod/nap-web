package net.nprod.nap.pages.partCondition

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.assertNotEquals
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import net.nprod.nap.types.PartCondition
import net.nprod.nap.types.Pharmacy

class PartConditionViewDataTest {
    
    @Test
    fun testPartConditionViewDataCreation() {
        val partCondition = PartCondition("https://nap.nprod.net/condition/fresh", "Fresh")
        val pharmacy1 = Pharmacy("https://nap.nprod.net/pharmacy/1")
        val pharmacy2 = Pharmacy("https://nap.nprod.net/pharmacy/2")
        val pharmacies = listOf(pharmacy1, pharmacy2)
        
        val viewData = PartConditionViewData(
            identifier = "fresh",
            partCondition = partCondition,
            pharmacies = pharmacies
        )
        
        assertEquals("fresh", viewData.identifier)
        assertEquals(partCondition, viewData.partCondition)
        assertEquals(pharmacies, viewData.pharmacies)
        assertEquals(2, viewData.pharmacies.size)
    }
    
    @Test
    fun testPartConditionViewDataWithEmptyPharmacies() {
        val partCondition = PartCondition("https://nap.nprod.net/condition/dried", "Dried")
        val viewData = PartConditionViewData(
            identifier = "dried",
            partCondition = partCondition,
            pharmacies = emptyList()
        )
        
        assertEquals("dried", viewData.identifier)
        assertEquals(partCondition, viewData.partCondition)
        assertTrue(viewData.pharmacies.isEmpty())
    }
    
    @Test
    fun testPartConditionViewDataEquality() {
        val condition1 = PartCondition("https://nap.nprod.net/condition/frozen", "Frozen")
        val condition2 = PartCondition("https://nap.nprod.net/condition/frozen", "Frozen")
        val pharmacy = Pharmacy("https://nap.nprod.net/pharmacy/100")
        
        val viewData1 = PartConditionViewData("frozen", condition1, listOf(pharmacy))
        val viewData2 = PartConditionViewData("frozen", condition2, listOf(pharmacy))
        val viewData3 = PartConditionViewData("fresh", condition1, listOf(pharmacy))
        
        assertEquals(viewData1, viewData2)
        assertNotEquals(viewData1, viewData3)
    }
    
    @Test
    fun testPartConditionViewDataCopy() {
        val partCondition = PartCondition("https://nap.nprod.net/condition/powdered", "Powdered")
        val pharmacy = Pharmacy("https://nap.nprod.net/pharmacy/200")
        val viewData = PartConditionViewData("powdered", partCondition, listOf(pharmacy))
        
        val newPharmacy = Pharmacy("https://nap.nprod.net/pharmacy/201")
        val copy = viewData.copy(pharmacies = listOf(pharmacy, newPharmacy))
        
        assertEquals(viewData.identifier, copy.identifier)
        assertEquals(viewData.partCondition, copy.partCondition)
        assertEquals(2, copy.pharmacies.size)
        assertNotEquals(viewData.pharmacies, copy.pharmacies)
    }
    
    @Test
    fun testPartConditionViewDataToString() {
        val partCondition = PartCondition("https://nap.nprod.net/condition/extracted", "Extracted")
        val pharmacy = Pharmacy("https://nap.nprod.net/pharmacy/300")
        val viewData = PartConditionViewData("extracted", partCondition, listOf(pharmacy))
        
        val string = viewData.toString()
        assertTrue(string.contains("PartConditionViewData"))
        assertTrue(string.contains("identifier=extracted"))
        assertTrue(string.contains("partCondition="))
        assertTrue(string.contains("pharmacies="))
    }
    
    @Test
    fun testPartConditionViewDataSerialization() {
        val partCondition = PartCondition("https://nap.nprod.net/condition/fermented", "Fermented")
        val pharmacy = Pharmacy("https://nap.nprod.net/pharmacy/400")
        val viewData = PartConditionViewData("fermented", partCondition, listOf(pharmacy))
        
        // Test serialization
        val json = Json.encodeToString(viewData)
        assertTrue(json.contains("\"identifier\":\"fermented\""))
        assertTrue(json.contains("\"partCondition\""))
        assertTrue(json.contains("\"pharmacies\""))
        
        // Test deserialization
        val decoded = Json.decodeFromString<PartConditionViewData>(json)
        assertEquals(viewData.identifier, decoded.identifier)
        assertEquals(viewData.partCondition.uri, decoded.partCondition.uri)
        assertEquals(viewData.partCondition.name, decoded.partCondition.name)
        assertEquals(viewData.pharmacies.size, decoded.pharmacies.size)
        assertEquals(viewData.pharmacies[0].uri, decoded.pharmacies[0].uri)
    }
    
    @Test
    fun testPartConditionViewDataHashCode() {
        val partCondition = PartCondition("https://nap.nprod.net/condition/processed", "Processed")
        val pharmacy = Pharmacy("https://nap.nprod.net/pharmacy/500")
        
        val viewData1 = PartConditionViewData("processed", partCondition, listOf(pharmacy))
        val viewData2 = PartConditionViewData("processed", partCondition, listOf(pharmacy))
        
        assertEquals(viewData1.hashCode(), viewData2.hashCode())
    }
    
    @Test
    fun testPartConditionViewDataWithMultiplePharmacies() {
        val partCondition = PartCondition("https://nap.nprod.net/condition/raw", "Raw")
        val pharmacies = (1..5).map { Pharmacy("https://nap.nprod.net/pharmacy/$it") }
        
        val viewData = PartConditionViewData("raw", partCondition, pharmacies)
        
        assertEquals(5, viewData.pharmacies.size)
        pharmacies.forEachIndexed { index, pharmacy ->
            assertEquals(pharmacy, viewData.pharmacies[index])
        }
    }
}