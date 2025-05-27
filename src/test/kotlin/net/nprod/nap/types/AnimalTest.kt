package net.nprod.nap.types

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.assertNotEquals
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString

class AnimalTest {
    
    @Test
    fun testAnimalCreation() {
        val animal = Animal(
            uri = "https://nap.nprod.net/animal/mouse",
            name = "Mouse"
        )
        
        assertEquals("https://nap.nprod.net/animal/mouse", animal.uri)
        assertEquals("Mouse", animal.name)
    }
    
    @Test
    fun testAnimalEquality() {
        val animal1 = Animal(
            uri = "https://nap.nprod.net/animal/rat",
            name = "Rat"
        )
        val animal2 = Animal(
            uri = "https://nap.nprod.net/animal/rat",
            name = "Rat"
        )
        val animal3 = Animal(
            uri = "https://nap.nprod.net/animal/rabbit",
            name = "Rabbit"
        )
        
        assertEquals(animal1, animal2)
        assertNotEquals(animal1, animal3)
    }
    
    @Test
    fun testAnimalCopy() {
        val animal = Animal(
            uri = "https://nap.nprod.net/animal/guinea-pig",
            name = "Guinea Pig"
        )
        
        val copy = animal.copy(name = "Guinea Pig (Cavia porcellus)")
        
        assertEquals(animal.uri, copy.uri)
        assertEquals("Guinea Pig (Cavia porcellus)", copy.name)
        assertNotEquals(animal, copy)
    }
    
    @Test
    fun testAnimalToString() {
        val animal = Animal(
            uri = "https://nap.nprod.net/animal/dog",
            name = "Dog"
        )
        
        val string = animal.toString()
        assertTrue(string.contains("Animal"))
        assertTrue(string.contains("uri=https://nap.nprod.net/animal/dog"))
        assertTrue(string.contains("name=Dog"))
    }
    
    @Test
    fun testAnimalSerialization() {
        val animal = Animal(
            uri = "https://nap.nprod.net/animal/cat",
            name = "Cat"
        )
        
        // Test serialization
        val json = Json.encodeToString(animal)
        assertTrue(json.contains("\"uri\":\"https://nap.nprod.net/animal/cat\""))
        assertTrue(json.contains("\"name\":\"Cat\""))
        
        // Test deserialization
        val decoded = Json.decodeFromString<Animal>(json)
        assertEquals(animal.uri, decoded.uri)
        assertEquals(animal.name, decoded.name)
        assertEquals(animal, decoded)
    }
    
    @Test
    fun testAnimalHashCode() {
        val animal1 = Animal(
            uri = "https://nap.nprod.net/animal/monkey",
            name = "Monkey"
        )
        val animal2 = Animal(
            uri = "https://nap.nprod.net/animal/monkey",
            name = "Monkey"
        )
        
        assertEquals(animal1.hashCode(), animal2.hashCode())
    }
    
    @Test
    fun testAnimalVariousSpecies() {
        val animals = listOf(
            Animal("https://nap.nprod.net/animal/mouse", "Mouse"),
            Animal("https://nap.nprod.net/animal/rat", "Rat"),
            Animal("https://nap.nprod.net/animal/guinea-pig", "Guinea Pig"),
            Animal("https://nap.nprod.net/animal/rabbit", "Rabbit"),
            Animal("https://nap.nprod.net/animal/dog", "Dog"),
            Animal("https://nap.nprod.net/animal/pig", "Pig"),
            Animal("https://nap.nprod.net/animal/sheep", "Sheep"),
            Animal("https://nap.nprod.net/animal/monkey", "Monkey")
        )
        
        assertEquals(8, animals.size)
        assertEquals(8, animals.distinct().size)
        assertTrue(animals.all { it.uri.startsWith("https://nap.nprod.net/animal/") })
    }
}