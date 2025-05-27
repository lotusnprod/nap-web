package net.nprod.nap.types

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.assertNotEquals
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString

class CountryTest {
    
    @Test
    fun testCountryCreation() {
        val country = Country(
            uri = "https://nap.nprod.net/country/usa",
            name = "United States"
        )
        
        assertEquals("https://nap.nprod.net/country/usa", country.uri)
        assertEquals("United States", country.name)
    }
    
    @Test
    fun testCountryEquality() {
        val country1 = Country(
            uri = "https://nap.nprod.net/country/canada",
            name = "Canada"
        )
        val country2 = Country(
            uri = "https://nap.nprod.net/country/canada",
            name = "Canada"
        )
        val country3 = Country(
            uri = "https://nap.nprod.net/country/mexico",
            name = "Mexico"
        )
        
        assertEquals(country1, country2)
        assertNotEquals(country1, country3)
    }
    
    @Test
    fun testCountryCopy() {
        val country = Country(
            uri = "https://nap.nprod.net/country/brazil",
            name = "Brazil"
        )
        
        val copy = country.copy(name = "Brasil")
        
        assertEquals(country.uri, copy.uri)
        assertEquals("Brasil", copy.name)
        assertNotEquals(country, copy)
    }
    
    @Test
    fun testCountryToString() {
        val country = Country(
            uri = "https://nap.nprod.net/country/france",
            name = "France"
        )
        
        val string = country.toString()
        assertTrue(string.contains("Country"))
        assertTrue(string.contains("uri=https://nap.nprod.net/country/france"))
        assertTrue(string.contains("name=France"))
    }
    
    @Test
    fun testCountrySerialization() {
        val country = Country(
            uri = "https://nap.nprod.net/country/japan",
            name = "Japan"
        )
        
        // Test serialization
        val json = Json.encodeToString(country)
        assertTrue(json.contains("\"uri\":\"https://nap.nprod.net/country/japan\""))
        assertTrue(json.contains("\"name\":\"Japan\""))
        
        // Test deserialization
        val decoded = Json.decodeFromString<Country>(json)
        assertEquals(country.uri, decoded.uri)
        assertEquals(country.name, decoded.name)
        assertEquals(country, decoded)
    }
    
    @Test
    fun testCountryHashCode() {
        val country1 = Country(
            uri = "https://nap.nprod.net/country/germany",
            name = "Germany"
        )
        val country2 = Country(
            uri = "https://nap.nprod.net/country/germany",
            name = "Germany"
        )
        
        assertEquals(country1.hashCode(), country2.hashCode())
    }
    
    @Test
    fun testCountryMutability() {
        val country = Country(
            uri = "https://nap.nprod.net/country/original",
            name = "Original Country"
        )
        
        // Test that properties are mutable (var)
        country.uri = "https://nap.nprod.net/country/modified"
        country.name = "Modified Country"
        
        assertEquals("https://nap.nprod.net/country/modified", country.uri)
        assertEquals("Modified Country", country.name)
    }
}