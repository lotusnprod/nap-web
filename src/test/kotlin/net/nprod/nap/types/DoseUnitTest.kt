package net.nprod.nap.types

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.assertNotEquals
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString

class DoseUnitTest {
    
    @Test
    fun testDoseUnitCreation() {
        val doseUnit = DoseUnit(
            uri = "https://nap.nprod.net/doseunit/mg",
            name = "milligram"
        )
        
        assertEquals("https://nap.nprod.net/doseunit/mg", doseUnit.uri)
        assertEquals("milligram", doseUnit.name)
    }
    
    @Test
    fun testDoseUnitEquality() {
        val unit1 = DoseUnit(
            uri = "https://nap.nprod.net/doseunit/g",
            name = "gram"
        )
        val unit2 = DoseUnit(
            uri = "https://nap.nprod.net/doseunit/g",
            name = "gram"
        )
        val unit3 = DoseUnit(
            uri = "https://nap.nprod.net/doseunit/kg",
            name = "kilogram"
        )
        
        assertEquals(unit1, unit2)
        assertNotEquals(unit1, unit3)
    }
    
    @Test
    fun testDoseUnitCopy() {
        val doseUnit = DoseUnit(
            uri = "https://nap.nprod.net/doseunit/ug",
            name = "microgram"
        )
        
        val copy = doseUnit.copy(name = "mcg")
        
        assertEquals(doseUnit.uri, copy.uri)
        assertEquals("mcg", copy.name)
        assertNotEquals(doseUnit, copy)
    }
    
    @Test
    fun testDoseUnitToString() {
        val doseUnit = DoseUnit(
            uri = "https://nap.nprod.net/doseunit/ml",
            name = "milliliter"
        )
        
        val string = doseUnit.toString()
        assertTrue(string.contains("DoseUnit"))
        assertTrue(string.contains("uri=https://nap.nprod.net/doseunit/ml"))
        assertTrue(string.contains("name=milliliter"))
    }
    
    @Test
    fun testDoseUnitSerialization() {
        val doseUnit = DoseUnit(
            uri = "https://nap.nprod.net/doseunit/l",
            name = "liter"
        )
        
        // Test serialization
        val json = Json.encodeToString(doseUnit)
        assertTrue(json.contains("\"uri\":\"https://nap.nprod.net/doseunit/l\""))
        assertTrue(json.contains("\"name\":\"liter\""))
        
        // Test deserialization
        val decoded = Json.decodeFromString<DoseUnit>(json)
        assertEquals(doseUnit.uri, decoded.uri)
        assertEquals(doseUnit.name, decoded.name)
        assertEquals(doseUnit, decoded)
    }
    
    @Test
    fun testDoseUnitHashCode() {
        val unit1 = DoseUnit(
            uri = "https://nap.nprod.net/doseunit/iu",
            name = "international unit"
        )
        val unit2 = DoseUnit(
            uri = "https://nap.nprod.net/doseunit/iu",
            name = "international unit"
        )
        
        assertEquals(unit1.hashCode(), unit2.hashCode())
    }
    
    @Test
    fun testDoseUnitVariousUnits() {
        val units = listOf(
            DoseUnit("https://nap.nprod.net/doseunit/mg", "milligram"),
            DoseUnit("https://nap.nprod.net/doseunit/g", "gram"),
            DoseUnit("https://nap.nprod.net/doseunit/kg", "kilogram"),
            DoseUnit("https://nap.nprod.net/doseunit/ml", "milliliter"),
            DoseUnit("https://nap.nprod.net/doseunit/l", "liter")
        )
        
        assertEquals(5, units.size)
        assertEquals(5, units.distinct().size)
        assertTrue(units.all { it.uri.startsWith("https://nap.nprod.net/doseunit/") })
    }
}