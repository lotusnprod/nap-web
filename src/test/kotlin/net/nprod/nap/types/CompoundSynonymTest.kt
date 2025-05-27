package net.nprod.nap.types

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.assertNotEquals
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString

class CompoundSynonymTest {
    
    @Test
    fun testCompoundSynonymCreation() {
        val synonym = CompoundSynonym(
            uri = "https://nap.nprod.net/synonym/aspirin",
            name = "Aspirin"
        )
        
        assertEquals("https://nap.nprod.net/synonym/aspirin", synonym.uri)
        assertEquals("Aspirin", synonym.name)
    }
    
    @Test
    fun testCompoundSynonymEquality() {
        val synonym1 = CompoundSynonym(
            uri = "https://nap.nprod.net/synonym/acetylsalicylic",
            name = "Acetylsalicylic acid"
        )
        val synonym2 = CompoundSynonym(
            uri = "https://nap.nprod.net/synonym/acetylsalicylic",
            name = "Acetylsalicylic acid"
        )
        val synonym3 = CompoundSynonym(
            uri = "https://nap.nprod.net/synonym/salicylic",
            name = "Salicylic acid"
        )
        
        assertEquals(synonym1, synonym2)
        assertNotEquals(synonym1, synonym3)
    }
    
    @Test
    fun testCompoundSynonymCopy() {
        val synonym = CompoundSynonym(
            uri = "https://nap.nprod.net/synonym/paracetamol",
            name = "Paracetamol"
        )
        
        val copy = synonym.copy(name = "Acetaminophen")
        
        assertEquals(synonym.uri, copy.uri)
        assertEquals("Acetaminophen", copy.name)
        assertNotEquals(synonym, copy)
    }
    
    @Test
    fun testCompoundSynonymToString() {
        val synonym = CompoundSynonym(
            uri = "https://nap.nprod.net/synonym/ibuprofen",
            name = "Ibuprofen"
        )
        
        val string = synonym.toString()
        assertTrue(string.contains("CompoundSynonym"))
        assertTrue(string.contains("uri=https://nap.nprod.net/synonym/ibuprofen"))
        assertTrue(string.contains("name=Ibuprofen"))
    }
    
    @Test
    fun testCompoundSynonymSerialization() {
        val synonym = CompoundSynonym(
            uri = "https://nap.nprod.net/synonym/caffeine",
            name = "Caffeine"
        )
        
        // Test serialization
        val json = Json.encodeToString(synonym)
        assertTrue(json.contains("\"uri\":\"https://nap.nprod.net/synonym/caffeine\""))
        assertTrue(json.contains("\"name\":\"Caffeine\""))
        
        // Test deserialization
        val decoded = Json.decodeFromString<CompoundSynonym>(json)
        assertEquals(synonym.uri, decoded.uri)
        assertEquals(synonym.name, decoded.name)
        assertEquals(synonym, decoded)
    }
    
    @Test
    fun testCompoundSynonymHashCode() {
        val synonym1 = CompoundSynonym(
            uri = "https://nap.nprod.net/synonym/morphine",
            name = "Morphine"
        )
        val synonym2 = CompoundSynonym(
            uri = "https://nap.nprod.net/synonym/morphine",
            name = "Morphine"
        )
        
        assertEquals(synonym1.hashCode(), synonym2.hashCode())
    }
    
    @Test
    fun testCompoundSynonymMutability() {
        val synonym = CompoundSynonym(
            uri = "https://nap.nprod.net/synonym/original",
            name = "Original Name"
        )
        
        // Test that properties are mutable (var)
        synonym.uri = "https://nap.nprod.net/synonym/modified"
        synonym.name = "Modified Name"
        
        assertEquals("https://nap.nprod.net/synonym/modified", synonym.uri)
        assertEquals("Modified Name", synonym.name)
    }
}