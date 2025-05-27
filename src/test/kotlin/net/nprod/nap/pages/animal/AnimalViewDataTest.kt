package net.nprod.nap.pages.animal

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.assertNotEquals
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import net.nprod.nap.types.Animal
import net.nprod.nap.types.Pharmacy

class AnimalViewDataTest {
    
    @Test
    fun testAnimalViewDataCreation() {
        val animal = Animal("https://nap.nprod.net/animal/mouse", "Mouse")
        val pharmacy1 = Pharmacy("https://nap.nprod.net/pharmacy/1")
        val pharmacy2 = Pharmacy("https://nap.nprod.net/pharmacy/2")
        val pharmacyResults = listOf(pharmacy1, pharmacy2)
        
        val viewData = AnimalViewData(
            identifier = "mouse",
            animal = animal,
            pharmacyResults = pharmacyResults
        )
        
        assertEquals("mouse", viewData.identifier)
        assertEquals(animal, viewData.animal)
        assertEquals(pharmacyResults, viewData.pharmacyResults)
        assertEquals(2, viewData.pharmacyResults.size)
    }
    
    @Test
    fun testAnimalViewDataWithEmptyPharmacyResults() {
        val animal = Animal("https://nap.nprod.net/animal/rat", "Rat")
        val viewData = AnimalViewData(
            identifier = "rat",
            animal = animal,
            pharmacyResults = emptyList()
        )
        
        assertEquals("rat", viewData.identifier)
        assertEquals(animal, viewData.animal)
        assertTrue(viewData.pharmacyResults.isEmpty())
    }
    
    @Test
    fun testAnimalViewDataEquality() {
        val animal1 = Animal("https://nap.nprod.net/animal/rabbit", "Rabbit")
        val animal2 = Animal("https://nap.nprod.net/animal/rabbit", "Rabbit")
        val pharmacy = Pharmacy("https://nap.nprod.net/pharmacy/100")
        
        val viewData1 = AnimalViewData("rabbit", animal1, listOf(pharmacy))
        val viewData2 = AnimalViewData("rabbit", animal2, listOf(pharmacy))
        val viewData3 = AnimalViewData("guinea-pig", animal1, listOf(pharmacy))
        
        assertEquals(viewData1, viewData2)
        assertNotEquals(viewData1, viewData3)
    }
    
    @Test
    fun testAnimalViewDataCopy() {
        val animal = Animal("https://nap.nprod.net/animal/dog", "Dog")
        val pharmacy = Pharmacy("https://nap.nprod.net/pharmacy/200")
        val viewData = AnimalViewData("dog", animal, listOf(pharmacy))
        
        val newPharmacy = Pharmacy("https://nap.nprod.net/pharmacy/201")
        val copy = viewData.copy(pharmacyResults = listOf(pharmacy, newPharmacy))
        
        assertEquals(viewData.identifier, copy.identifier)
        assertEquals(viewData.animal, copy.animal)
        assertEquals(2, copy.pharmacyResults.size)
        assertNotEquals(viewData.pharmacyResults, copy.pharmacyResults)
    }
    
    @Test
    fun testAnimalViewDataToString() {
        val animal = Animal("https://nap.nprod.net/animal/cat", "Cat")
        val pharmacy = Pharmacy("https://nap.nprod.net/pharmacy/300")
        val viewData = AnimalViewData("cat", animal, listOf(pharmacy))
        
        val string = viewData.toString()
        assertTrue(string.contains("AnimalViewData"))
        assertTrue(string.contains("identifier=cat"))
        assertTrue(string.contains("animal="))
        assertTrue(string.contains("pharmacyResults="))
    }
    
    @Test
    fun testAnimalViewDataSerialization() {
        val animal = Animal("https://nap.nprod.net/animal/monkey", "Monkey")
        val pharmacy = Pharmacy("https://nap.nprod.net/pharmacy/400")
        val viewData = AnimalViewData("monkey", animal, listOf(pharmacy))
        
        // Test serialization
        val json = Json.encodeToString(viewData)
        assertTrue(json.contains("\"identifier\":\"monkey\""))
        assertTrue(json.contains("\"animal\""))
        assertTrue(json.contains("\"pharmacyResults\""))
        
        // Test deserialization
        val decoded = Json.decodeFromString<AnimalViewData>(json)
        assertEquals(viewData.identifier, decoded.identifier)
        assertEquals(viewData.animal.uri, decoded.animal.uri)
        assertEquals(viewData.animal.name, decoded.animal.name)
        assertEquals(viewData.pharmacyResults.size, decoded.pharmacyResults.size)
        assertEquals(viewData.pharmacyResults[0].uri, decoded.pharmacyResults[0].uri)
    }
    
    @Test
    fun testAnimalViewDataHashCode() {
        val animal = Animal("https://nap.nprod.net/animal/sheep", "Sheep")
        val pharmacy = Pharmacy("https://nap.nprod.net/pharmacy/500")
        
        val viewData1 = AnimalViewData("sheep", animal, listOf(pharmacy))
        val viewData2 = AnimalViewData("sheep", animal, listOf(pharmacy))
        
        assertEquals(viewData1.hashCode(), viewData2.hashCode())
    }
    
    @Test
    fun testAnimalViewDataWithMultiplePharmacyResults() {
        val animal = Animal("https://nap.nprod.net/animal/pig", "Pig")
        val pharmacyResults = (1..5).map { Pharmacy("https://nap.nprod.net/pharmacy/$it") }
        
        val viewData = AnimalViewData("pig", animal, pharmacyResults)
        
        assertEquals(5, viewData.pharmacyResults.size)
        pharmacyResults.forEachIndexed { index, pharmacy ->
            assertEquals(pharmacy, viewData.pharmacyResults[index])
        }
    }
}