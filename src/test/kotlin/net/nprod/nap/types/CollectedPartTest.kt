package net.nprod.nap.types

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.assertNotEquals
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString

class CollectedPartTest {
    
    @Test
    fun testCollectedPartCreation() {
        val part = CollectedPart(
            uri = "https://nap.nprod.net/part/leaves",
            name = "Leaves"
        )
        
        assertEquals("https://nap.nprod.net/part/leaves", part.uri)
        assertEquals("Leaves", part.name)
    }
    
    @Test
    fun testCollectedPartEquality() {
        val part1 = CollectedPart(
            uri = "https://nap.nprod.net/part/roots",
            name = "Roots"
        )
        val part2 = CollectedPart(
            uri = "https://nap.nprod.net/part/roots",
            name = "Roots"
        )
        val part3 = CollectedPart(
            uri = "https://nap.nprod.net/part/stems",
            name = "Stems"
        )
        
        assertEquals(part1, part2)
        assertNotEquals(part1, part3)
    }
    
    @Test
    fun testCollectedPartCopy() {
        val part = CollectedPart(
            uri = "https://nap.nprod.net/part/flowers",
            name = "Flowers"
        )
        
        val copy = part.copy(name = "Modified Flowers")
        
        assertEquals(part.uri, copy.uri)
        assertEquals("Modified Flowers", copy.name)
        assertNotEquals(part, copy)
    }
    
    @Test
    fun testCollectedPartToString() {
        val part = CollectedPart(
            uri = "https://nap.nprod.net/part/fruits",
            name = "Fruits"
        )
        
        val string = part.toString()
        assertTrue(string.contains("CollectedPart"))
        assertTrue(string.contains("uri=https://nap.nprod.net/part/fruits"))
        assertTrue(string.contains("name=Fruits"))
    }
    
    @Test
    fun testCollectedPartSerialization() {
        val part = CollectedPart(
            uri = "https://nap.nprod.net/part/seeds",
            name = "Seeds"
        )
        
        // Test serialization
        val json = Json.encodeToString(part)
        assertTrue(json.contains("\"uri\":\"https://nap.nprod.net/part/seeds\""))
        assertTrue(json.contains("\"name\":\"Seeds\""))
        
        // Test deserialization
        val decoded = Json.decodeFromString<CollectedPart>(json)
        assertEquals(part.uri, decoded.uri)
        assertEquals(part.name, decoded.name)
        assertEquals(part, decoded)
    }
    
    @Test
    fun testCollectedPartHashCode() {
        val part1 = CollectedPart(
            uri = "https://nap.nprod.net/part/bark",
            name = "Bark"
        )
        val part2 = CollectedPart(
            uri = "https://nap.nprod.net/part/bark",
            name = "Bark"
        )
        
        assertEquals(part1.hashCode(), part2.hashCode())
    }
}