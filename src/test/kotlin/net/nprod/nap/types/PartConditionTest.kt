package net.nprod.nap.types

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.assertNotEquals
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString

class PartConditionTest {
    
    @Test
    fun testPartConditionCreation() {
        val condition = PartCondition(
            uri = "https://nap.nprod.net/condition/fresh",
            name = "Fresh"
        )
        
        assertEquals("https://nap.nprod.net/condition/fresh", condition.uri)
        assertEquals("Fresh", condition.name)
    }
    
    @Test
    fun testPartConditionEquality() {
        val condition1 = PartCondition(
            uri = "https://nap.nprod.net/condition/dried",
            name = "Dried"
        )
        val condition2 = PartCondition(
            uri = "https://nap.nprod.net/condition/dried",
            name = "Dried"
        )
        val condition3 = PartCondition(
            uri = "https://nap.nprod.net/condition/frozen",
            name = "Frozen"
        )
        
        assertEquals(condition1, condition2)
        assertNotEquals(condition1, condition3)
    }
    
    @Test
    fun testPartConditionCopy() {
        val condition = PartCondition(
            uri = "https://nap.nprod.net/condition/powdered",
            name = "Powdered"
        )
        
        val copy = condition.copy(name = "Finely Powdered")
        
        assertEquals(condition.uri, copy.uri)
        assertEquals("Finely Powdered", copy.name)
        assertNotEquals(condition, copy)
    }
    
    @Test
    fun testPartConditionToString() {
        val condition = PartCondition(
            uri = "https://nap.nprod.net/condition/fermented",
            name = "Fermented"
        )
        
        val string = condition.toString()
        assertTrue(string.contains("PartCondition"))
        assertTrue(string.contains("uri=https://nap.nprod.net/condition/fermented"))
        assertTrue(string.contains("name=Fermented"))
    }
    
    @Test
    fun testPartConditionSerialization() {
        val condition = PartCondition(
            uri = "https://nap.nprod.net/condition/extracted",
            name = "Extracted"
        )
        
        // Test serialization
        val json = Json.encodeToString(condition)
        assertTrue(json.contains("\"uri\":\"https://nap.nprod.net/condition/extracted\""))
        assertTrue(json.contains("\"name\":\"Extracted\""))
        
        // Test deserialization
        val decoded = Json.decodeFromString<PartCondition>(json)
        assertEquals(condition.uri, decoded.uri)
        assertEquals(condition.name, decoded.name)
        assertEquals(condition, decoded)
    }
    
    @Test
    fun testPartConditionHashCode() {
        val condition1 = PartCondition(
            uri = "https://nap.nprod.net/condition/processed",
            name = "Processed"
        )
        val condition2 = PartCondition(
            uri = "https://nap.nprod.net/condition/processed",
            name = "Processed"
        )
        
        assertEquals(condition1.hashCode(), condition2.hashCode())
    }
}