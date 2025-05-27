package net.nprod.nap.types

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.assertNotEquals
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString

class OrganismClassTest {
    
    @Test
    fun testOrganismClassCreation() {
        val organismClass = OrganismClass(
            uri = "https://nap.nprod.net/class/plantae",
            name = "Plantae"
        )
        
        assertEquals("https://nap.nprod.net/class/plantae", organismClass.uri)
        assertEquals("Plantae", organismClass.name)
    }
    
    @Test
    fun testOrganismClassEquality() {
        val class1 = OrganismClass(
            uri = "https://nap.nprod.net/class/animalia",
            name = "Animalia"
        )
        val class2 = OrganismClass(
            uri = "https://nap.nprod.net/class/animalia",
            name = "Animalia"
        )
        val class3 = OrganismClass(
            uri = "https://nap.nprod.net/class/fungi",
            name = "Fungi"
        )
        
        assertEquals(class1, class2)
        assertNotEquals(class1, class3)
    }
    
    @Test
    fun testOrganismClassCopy() {
        val organismClass = OrganismClass(
            uri = "https://nap.nprod.net/class/bacteria",
            name = "Bacteria"
        )
        
        val copy = organismClass.copy(name = "Modified Bacteria")
        
        assertEquals(organismClass.uri, copy.uri)
        assertEquals("Modified Bacteria", copy.name)
        assertNotEquals(organismClass, copy)
    }
    
    @Test
    fun testOrganismClassToString() {
        val organismClass = OrganismClass(
            uri = "https://nap.nprod.net/class/archaea",
            name = "Archaea"
        )
        
        val string = organismClass.toString()
        assertTrue(string.contains("OrganismClass"))
        assertTrue(string.contains("uri=https://nap.nprod.net/class/archaea"))
        assertTrue(string.contains("name=Archaea"))
    }
    
    @Test
    fun testOrganismClassSerialization() {
        val organismClass = OrganismClass(
            uri = "https://nap.nprod.net/class/protozoa",
            name = "Protozoa"
        )
        
        // Test serialization
        val json = Json.encodeToString(organismClass)
        assertTrue(json.contains("\"uri\":\"https://nap.nprod.net/class/protozoa\""))
        assertTrue(json.contains("\"name\":\"Protozoa\""))
        
        // Test deserialization
        val decoded = Json.decodeFromString<OrganismClass>(json)
        assertEquals(organismClass.uri, decoded.uri)
        assertEquals(organismClass.name, decoded.name)
        assertEquals(organismClass, decoded)
    }
    
    @Test
    fun testOrganismClassHashCode() {
        val class1 = OrganismClass(
            uri = "https://nap.nprod.net/class/chromista",
            name = "Chromista"
        )
        val class2 = OrganismClass(
            uri = "https://nap.nprod.net/class/chromista",
            name = "Chromista"
        )
        
        assertEquals(class1.hashCode(), class2.hashCode())
    }
    
    @Test
    fun testOrganismClassMutability() {
        val organismClass = OrganismClass(
            uri = "https://nap.nprod.net/class/original",
            name = "Original Class"
        )
        
        // Test that properties are mutable (var)
        organismClass.uri = "https://nap.nprod.net/class/modified"
        organismClass.name = "Modified Class"
        
        assertEquals("https://nap.nprod.net/class/modified", organismClass.uri)
        assertEquals("Modified Class", organismClass.name)
    }
}