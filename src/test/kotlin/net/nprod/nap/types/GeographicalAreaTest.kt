package net.nprod.nap.types

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.assertNotEquals
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString

class GeographicalAreaTest {
    
    @Test
    fun testGeographicalAreaCreation() {
        val area = GeographicalArea(
            uri = "https://nap.nprod.net/area/north-america",
            name = "North America"
        )
        
        assertEquals("https://nap.nprod.net/area/north-america", area.uri)
        assertEquals("North America", area.name)
    }
    
    @Test
    fun testGeographicalAreaEquality() {
        val area1 = GeographicalArea(
            uri = "https://nap.nprod.net/area/europe",
            name = "Europe"
        )
        val area2 = GeographicalArea(
            uri = "https://nap.nprod.net/area/europe",
            name = "Europe"
        )
        val area3 = GeographicalArea(
            uri = "https://nap.nprod.net/area/asia",
            name = "Asia"
        )
        
        assertEquals(area1, area2)
        assertNotEquals(area1, area3)
    }
    
    @Test
    fun testGeographicalAreaCopy() {
        val area = GeographicalArea(
            uri = "https://nap.nprod.net/area/africa",
            name = "Africa"
        )
        
        val copy = area.copy(name = "African Continent")
        
        assertEquals(area.uri, copy.uri)
        assertEquals("African Continent", copy.name)
        assertNotEquals(area, copy)
    }
    
    @Test
    fun testGeographicalAreaToString() {
        val area = GeographicalArea(
            uri = "https://nap.nprod.net/area/south-america",
            name = "South America"
        )
        
        val string = area.toString()
        assertTrue(string.contains("GeographicalArea"))
        assertTrue(string.contains("uri=https://nap.nprod.net/area/south-america"))
        assertTrue(string.contains("name=South America"))
    }
    
    @Test
    fun testGeographicalAreaSerialization() {
        val area = GeographicalArea(
            uri = "https://nap.nprod.net/area/oceania",
            name = "Oceania"
        )
        
        // Test serialization
        val json = Json.encodeToString(area)
        assertTrue(json.contains("\"uri\":\"https://nap.nprod.net/area/oceania\""))
        assertTrue(json.contains("\"name\":\"Oceania\""))
        
        // Test deserialization
        val decoded = Json.decodeFromString<GeographicalArea>(json)
        assertEquals(area.uri, decoded.uri)
        assertEquals(area.name, decoded.name)
        assertEquals(area, decoded)
    }
    
    @Test
    fun testGeographicalAreaHashCode() {
        val area1 = GeographicalArea(
            uri = "https://nap.nprod.net/area/antarctica",
            name = "Antarctica"
        )
        val area2 = GeographicalArea(
            uri = "https://nap.nprod.net/area/antarctica",
            name = "Antarctica"
        )
        
        assertEquals(area1.hashCode(), area2.hashCode())
    }
    
    @Test
    fun testGeographicalAreaMutability() {
        val area = GeographicalArea(
            uri = "https://nap.nprod.net/area/original",
            name = "Original Area"
        )
        
        // Test that properties are mutable (var)
        area.uri = "https://nap.nprod.net/area/modified"
        area.name = "Modified Area"
        
        assertEquals("https://nap.nprod.net/area/modified", area.uri)
        assertEquals("Modified Area", area.name)
    }
}