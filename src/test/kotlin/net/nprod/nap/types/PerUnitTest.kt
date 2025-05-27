package net.nprod.nap.types

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.assertNotEquals
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString

class PerUnitTest {
    
    @Test
    fun testPerUnitCreation() {
        val perUnit = PerUnit(
            uri = "https://nap.nprod.net/perunit/kg",
            name = "per kilogram"
        )
        
        assertEquals("https://nap.nprod.net/perunit/kg", perUnit.uri)
        assertEquals("per kilogram", perUnit.name)
    }
    
    @Test
    fun testPerUnitEquality() {
        val unit1 = PerUnit(
            uri = "https://nap.nprod.net/perunit/day",
            name = "per day"
        )
        val unit2 = PerUnit(
            uri = "https://nap.nprod.net/perunit/day",
            name = "per day"
        )
        val unit3 = PerUnit(
            uri = "https://nap.nprod.net/perunit/hour",
            name = "per hour"
        )
        
        assertEquals(unit1, unit2)
        assertNotEquals(unit1, unit3)
    }
    
    @Test
    fun testPerUnitCopy() {
        val perUnit = PerUnit(
            uri = "https://nap.nprod.net/perunit/dose",
            name = "per dose"
        )
        
        val copy = perUnit.copy(name = "per single dose")
        
        assertEquals(perUnit.uri, copy.uri)
        assertEquals("per single dose", copy.name)
        assertNotEquals(perUnit, copy)
    }
    
    @Test
    fun testPerUnitToString() {
        val perUnit = PerUnit(
            uri = "https://nap.nprod.net/perunit/week",
            name = "per week"
        )
        
        val string = perUnit.toString()
        assertTrue(string.contains("PerUnit"))
        assertTrue(string.contains("uri=https://nap.nprod.net/perunit/week"))
        assertTrue(string.contains("name=per week"))
    }
    
    @Test
    fun testPerUnitSerialization() {
        val perUnit = PerUnit(
            uri = "https://nap.nprod.net/perunit/month",
            name = "per month"
        )
        
        // Test serialization
        val json = Json.encodeToString(perUnit)
        assertTrue(json.contains("\"uri\":\"https://nap.nprod.net/perunit/month\""))
        assertTrue(json.contains("\"name\":\"per month\""))
        
        // Test deserialization
        val decoded = Json.decodeFromString<PerUnit>(json)
        assertEquals(perUnit.uri, decoded.uri)
        assertEquals(perUnit.name, decoded.name)
        assertEquals(perUnit, decoded)
    }
    
    @Test
    fun testPerUnitHashCode() {
        val unit1 = PerUnit(
            uri = "https://nap.nprod.net/perunit/year",
            name = "per year"
        )
        val unit2 = PerUnit(
            uri = "https://nap.nprod.net/perunit/year",
            name = "per year"
        )
        
        assertEquals(unit1.hashCode(), unit2.hashCode())
    }
    
    @Test
    fun testPerUnitVariousUnits() {
        val units = listOf(
            PerUnit("https://nap.nprod.net/perunit/kg", "per kilogram"),
            PerUnit("https://nap.nprod.net/perunit/day", "per day"),
            PerUnit("https://nap.nprod.net/perunit/dose", "per dose"),
            PerUnit("https://nap.nprod.net/perunit/m2", "per square meter"),
            PerUnit("https://nap.nprod.net/perunit/100g", "per 100 grams")
        )
        
        assertEquals(5, units.size)
        assertEquals(5, units.distinct().size)
        assertTrue(units.all { it.uri.startsWith("https://nap.nprod.net/perunit/") })
    }
}