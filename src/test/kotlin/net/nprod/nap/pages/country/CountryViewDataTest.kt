package net.nprod.nap.pages.country

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.assertNotEquals
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import net.nprod.nap.types.Country
import net.nprod.nap.types.Pharmacy

class CountryViewDataTest {
    
    @Test
    fun testCountryViewDataCreation() {
        val country = Country("https://nap.nprod.net/country/usa", "United States")
        val pharmacy1 = Pharmacy("https://nap.nprod.net/pharmacy/1")
        val pharmacy2 = Pharmacy("https://nap.nprod.net/pharmacy/2")
        val pharmacies = listOf(pharmacy1, pharmacy2)
        
        val viewData = CountryViewData(
            identifier = "usa",
            country = country,
            pharmacies = pharmacies
        )
        
        assertEquals("usa", viewData.identifier)
        assertEquals(country, viewData.country)
        assertEquals(pharmacies, viewData.pharmacies)
        assertEquals(2, viewData.pharmacies.size)
    }
    
    @Test
    fun testCountryViewDataWithEmptyPharmacies() {
        val country = Country("https://nap.nprod.net/country/canada", "Canada")
        val viewData = CountryViewData(
            identifier = "canada",
            country = country,
            pharmacies = emptyList()
        )
        
        assertEquals("canada", viewData.identifier)
        assertEquals(country, viewData.country)
        assertTrue(viewData.pharmacies.isEmpty())
    }
    
    @Test
    fun testCountryViewDataEquality() {
        val country1 = Country("https://nap.nprod.net/country/france", "France")
        val country2 = Country("https://nap.nprod.net/country/france", "France")
        val pharmacy = Pharmacy("https://nap.nprod.net/pharmacy/100")
        
        val viewData1 = CountryViewData("france", country1, listOf(pharmacy))
        val viewData2 = CountryViewData("france", country2, listOf(pharmacy))
        val viewData3 = CountryViewData("germany", country1, listOf(pharmacy))
        
        assertEquals(viewData1, viewData2)
        assertNotEquals(viewData1, viewData3)
    }
    
    @Test
    fun testCountryViewDataCopy() {
        val country = Country("https://nap.nprod.net/country/japan", "Japan")
        val pharmacy = Pharmacy("https://nap.nprod.net/pharmacy/200")
        val viewData = CountryViewData("japan", country, listOf(pharmacy))
        
        val newPharmacy = Pharmacy("https://nap.nprod.net/pharmacy/201")
        val copy = viewData.copy(pharmacies = listOf(pharmacy, newPharmacy))
        
        assertEquals(viewData.identifier, copy.identifier)
        assertEquals(viewData.country, copy.country)
        assertEquals(2, copy.pharmacies.size)
        assertNotEquals(viewData.pharmacies, copy.pharmacies)
    }
    
    @Test
    fun testCountryViewDataToString() {
        val country = Country("https://nap.nprod.net/country/brazil", "Brazil")
        val pharmacy = Pharmacy("https://nap.nprod.net/pharmacy/300")
        val viewData = CountryViewData("brazil", country, listOf(pharmacy))
        
        val string = viewData.toString()
        assertTrue(string.contains("CountryViewData"))
        assertTrue(string.contains("identifier=brazil"))
        assertTrue(string.contains("country="))
        assertTrue(string.contains("pharmacies="))
    }
    
    @Test
    fun testCountryViewDataSerialization() {
        val country = Country("https://nap.nprod.net/country/mexico", "Mexico")
        val pharmacy = Pharmacy("https://nap.nprod.net/pharmacy/400")
        val viewData = CountryViewData("mexico", country, listOf(pharmacy))
        
        // Test serialization
        val json = Json.encodeToString(viewData)
        assertTrue(json.contains("\"identifier\":\"mexico\""))
        assertTrue(json.contains("\"country\""))
        assertTrue(json.contains("\"pharmacies\""))
        
        // Test deserialization
        val decoded = Json.decodeFromString<CountryViewData>(json)
        assertEquals(viewData.identifier, decoded.identifier)
        assertEquals(viewData.country.uri, decoded.country.uri)
        assertEquals(viewData.country.name, decoded.country.name)
        assertEquals(viewData.pharmacies.size, decoded.pharmacies.size)
        assertEquals(viewData.pharmacies[0].uri, decoded.pharmacies[0].uri)
    }
    
    @Test
    fun testCountryViewDataHashCode() {
        val country = Country("https://nap.nprod.net/country/spain", "Spain")
        val pharmacy = Pharmacy("https://nap.nprod.net/pharmacy/500")
        
        val viewData1 = CountryViewData("spain", country, listOf(pharmacy))
        val viewData2 = CountryViewData("spain", country, listOf(pharmacy))
        
        assertEquals(viewData1.hashCode(), viewData2.hashCode())
    }
}