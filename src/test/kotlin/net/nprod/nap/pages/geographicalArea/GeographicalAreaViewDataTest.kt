package net.nprod.nap.pages.geographicalArea

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.assertNotEquals
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import net.nprod.nap.types.GeographicalArea
import net.nprod.nap.types.Pharmacy

class GeographicalAreaViewDataTest {
    
    @Test
    fun testGeographicalAreaViewDataCreation() {
        val area = GeographicalArea("https://nap.nprod.net/area/north-america", "North America")
        val pharmacy1 = Pharmacy("https://nap.nprod.net/pharmacy/1")
        val pharmacy2 = Pharmacy("https://nap.nprod.net/pharmacy/2")
        val pharmacies = listOf(pharmacy1, pharmacy2)
        
        val viewData = GeographicalAreaViewData(
            identifier = "north-america",
            geographicalArea = area,
            pharmacies = pharmacies
        )
        
        assertEquals("north-america", viewData.identifier)
        assertEquals(area, viewData.geographicalArea)
        assertEquals(pharmacies, viewData.pharmacies)
        assertEquals(2, viewData.pharmacies.size)
    }
    
    @Test
    fun testGeographicalAreaViewDataWithEmptyPharmacies() {
        val area = GeographicalArea("https://nap.nprod.net/area/antarctica", "Antarctica")
        val viewData = GeographicalAreaViewData(
            identifier = "antarctica",
            geographicalArea = area,
            pharmacies = emptyList()
        )
        
        assertEquals("antarctica", viewData.identifier)
        assertEquals(area, viewData.geographicalArea)
        assertTrue(viewData.pharmacies.isEmpty())
    }
    
    @Test
    fun testGeographicalAreaViewDataEquality() {
        val area1 = GeographicalArea("https://nap.nprod.net/area/europe", "Europe")
        val area2 = GeographicalArea("https://nap.nprod.net/area/europe", "Europe")
        val pharmacy = Pharmacy("https://nap.nprod.net/pharmacy/100")
        
        val viewData1 = GeographicalAreaViewData("europe", area1, listOf(pharmacy))
        val viewData2 = GeographicalAreaViewData("europe", area2, listOf(pharmacy))
        val viewData3 = GeographicalAreaViewData("asia", area1, listOf(pharmacy))
        
        assertEquals(viewData1, viewData2)
        assertNotEquals(viewData1, viewData3)
    }
    
    @Test
    fun testGeographicalAreaViewDataCopy() {
        val area = GeographicalArea("https://nap.nprod.net/area/africa", "Africa")
        val pharmacy = Pharmacy("https://nap.nprod.net/pharmacy/200")
        val viewData = GeographicalAreaViewData("africa", area, listOf(pharmacy))
        
        val newPharmacy = Pharmacy("https://nap.nprod.net/pharmacy/201")
        val copy = viewData.copy(pharmacies = listOf(pharmacy, newPharmacy))
        
        assertEquals(viewData.identifier, copy.identifier)
        assertEquals(viewData.geographicalArea, copy.geographicalArea)
        assertEquals(2, copy.pharmacies.size)
        assertNotEquals(viewData.pharmacies, copy.pharmacies)
    }
    
    @Test
    fun testGeographicalAreaViewDataToString() {
        val area = GeographicalArea("https://nap.nprod.net/area/oceania", "Oceania")
        val pharmacy = Pharmacy("https://nap.nprod.net/pharmacy/300")
        val viewData = GeographicalAreaViewData("oceania", area, listOf(pharmacy))
        
        val string = viewData.toString()
        assertTrue(string.contains("GeographicalAreaViewData"))
        assertTrue(string.contains("identifier=oceania"))
        assertTrue(string.contains("geographicalArea="))
        assertTrue(string.contains("pharmacies="))
    }
    
    @Test
    fun testGeographicalAreaViewDataSerialization() {
        val area = GeographicalArea("https://nap.nprod.net/area/south-america", "South America")
        val pharmacy = Pharmacy("https://nap.nprod.net/pharmacy/400")
        val viewData = GeographicalAreaViewData("south-america", area, listOf(pharmacy))
        
        // Test serialization
        val json = Json.encodeToString(viewData)
        assertTrue(json.contains("\"identifier\":\"south-america\""))
        assertTrue(json.contains("\"geographicalArea\""))
        assertTrue(json.contains("\"pharmacies\""))
        
        // Test deserialization
        val decoded = Json.decodeFromString<GeographicalAreaViewData>(json)
        assertEquals(viewData.identifier, decoded.identifier)
        assertEquals(viewData.geographicalArea.uri, decoded.geographicalArea.uri)
        assertEquals(viewData.geographicalArea.name, decoded.geographicalArea.name)
        assertEquals(viewData.pharmacies.size, decoded.pharmacies.size)
        assertEquals(viewData.pharmacies[0].uri, decoded.pharmacies[0].uri)
    }
    
    @Test
    fun testGeographicalAreaViewDataHashCode() {
        val area = GeographicalArea("https://nap.nprod.net/area/central-america", "Central America")
        val pharmacy = Pharmacy("https://nap.nprod.net/pharmacy/500")
        
        val viewData1 = GeographicalAreaViewData("central-america", area, listOf(pharmacy))
        val viewData2 = GeographicalAreaViewData("central-america", area, listOf(pharmacy))
        
        assertEquals(viewData1.hashCode(), viewData2.hashCode())
    }
    
    @Test
    fun testGeographicalAreaViewDataWithMultiplePharmacies() {
        val area = GeographicalArea("https://nap.nprod.net/area/caribbean", "Caribbean")
        val pharmacies = (1..5).map { Pharmacy("https://nap.nprod.net/pharmacy/$it") }
        
        val viewData = GeographicalAreaViewData("caribbean", area, pharmacies)
        
        assertEquals(5, viewData.pharmacies.size)
        pharmacies.forEachIndexed { index, pharmacy ->
            assertEquals(pharmacy, viewData.pharmacies[index])
        }
    }
}