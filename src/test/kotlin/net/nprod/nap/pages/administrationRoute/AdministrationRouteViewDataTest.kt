package net.nprod.nap.pages.administrationRoute

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.assertNotEquals
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import net.nprod.nap.types.AdministrationRoute
import net.nprod.nap.types.Pharmacy

class AdministrationRouteViewDataTest {
    
    @Test
    fun testAdministrationRouteViewDataCreation() {
        val route = AdministrationRoute("https://nap.nprod.net/administration/oral", "Oral")
        val pharmacy1 = Pharmacy("https://nap.nprod.net/pharmacy/1")
        val pharmacy2 = Pharmacy("https://nap.nprod.net/pharmacy/2")
        val pharmacyResults = listOf(pharmacy1, pharmacy2)
        
        val viewData = AdministrationRouteViewData(
            identifier = "oral",
            administrationRoute = route,
            pharmacyResults = pharmacyResults
        )
        
        assertEquals("oral", viewData.identifier)
        assertEquals(route, viewData.administrationRoute)
        assertEquals(pharmacyResults, viewData.pharmacyResults)
        assertEquals(2, viewData.pharmacyResults.size)
    }
    
    @Test
    fun testAdministrationRouteViewDataWithEmptyPharmacyResults() {
        val route = AdministrationRoute("https://nap.nprod.net/administration/topical", "Topical")
        val viewData = AdministrationRouteViewData(
            identifier = "topical",
            administrationRoute = route,
            pharmacyResults = emptyList()
        )
        
        assertEquals("topical", viewData.identifier)
        assertEquals(route, viewData.administrationRoute)
        assertTrue(viewData.pharmacyResults.isEmpty())
    }
    
    @Test
    fun testAdministrationRouteViewDataEquality() {
        val route1 = AdministrationRoute("https://nap.nprod.net/administration/injection", "Injection")
        val route2 = AdministrationRoute("https://nap.nprod.net/administration/injection", "Injection")
        val pharmacy = Pharmacy("https://nap.nprod.net/pharmacy/100")
        
        val viewData1 = AdministrationRouteViewData("injection", route1, listOf(pharmacy))
        val viewData2 = AdministrationRouteViewData("injection", route2, listOf(pharmacy))
        val viewData3 = AdministrationRouteViewData("inhalation", route1, listOf(pharmacy))
        
        assertEquals(viewData1, viewData2)
        assertNotEquals(viewData1, viewData3)
    }
    
    @Test
    fun testAdministrationRouteViewDataCopy() {
        val route = AdministrationRoute("https://nap.nprod.net/administration/sublingual", "Sublingual")
        val pharmacy = Pharmacy("https://nap.nprod.net/pharmacy/200")
        val viewData = AdministrationRouteViewData("sublingual", route, listOf(pharmacy))
        
        val newPharmacy = Pharmacy("https://nap.nprod.net/pharmacy/201")
        val copy = viewData.copy(pharmacyResults = listOf(pharmacy, newPharmacy))
        
        assertEquals(viewData.identifier, copy.identifier)
        assertEquals(viewData.administrationRoute, copy.administrationRoute)
        assertEquals(2, copy.pharmacyResults.size)
        assertNotEquals(viewData.pharmacyResults, copy.pharmacyResults)
    }
    
    @Test
    fun testAdministrationRouteViewDataToString() {
        val route = AdministrationRoute("https://nap.nprod.net/administration/transdermal", "Transdermal")
        val pharmacy = Pharmacy("https://nap.nprod.net/pharmacy/300")
        val viewData = AdministrationRouteViewData("transdermal", route, listOf(pharmacy))
        
        val string = viewData.toString()
        assertTrue(string.contains("AdministrationRouteViewData"))
        assertTrue(string.contains("identifier=transdermal"))
        assertTrue(string.contains("administrationRoute="))
        assertTrue(string.contains("pharmacyResults="))
    }
    
    @Test
    fun testAdministrationRouteViewDataSerialization() {
        val route = AdministrationRoute("https://nap.nprod.net/administration/ocular", "Ocular")
        val pharmacy = Pharmacy("https://nap.nprod.net/pharmacy/400")
        val viewData = AdministrationRouteViewData("ocular", route, listOf(pharmacy))
        
        // Test serialization
        val json = Json.encodeToString(viewData)
        assertTrue(json.contains("\"identifier\":\"ocular\""))
        assertTrue(json.contains("\"administrationRoute\""))
        assertTrue(json.contains("\"pharmacyResults\""))
        
        // Test deserialization
        val decoded = Json.decodeFromString<AdministrationRouteViewData>(json)
        assertEquals(viewData.identifier, decoded.identifier)
        assertEquals(viewData.administrationRoute.uri, decoded.administrationRoute.uri)
        assertEquals(viewData.administrationRoute.name, decoded.administrationRoute.name)
        assertEquals(viewData.pharmacyResults.size, decoded.pharmacyResults.size)
        assertEquals(viewData.pharmacyResults[0].uri, decoded.pharmacyResults[0].uri)
    }
    
    @Test
    fun testAdministrationRouteViewDataHashCode() {
        val route = AdministrationRoute("https://nap.nprod.net/administration/nasal", "Nasal")
        val pharmacy = Pharmacy("https://nap.nprod.net/pharmacy/500")
        
        val viewData1 = AdministrationRouteViewData("nasal", route, listOf(pharmacy))
        val viewData2 = AdministrationRouteViewData("nasal", route, listOf(pharmacy))
        
        assertEquals(viewData1.hashCode(), viewData2.hashCode())
    }
    
    @Test
    fun testAdministrationRouteViewDataWithMultiplePharmacyResults() {
        val route = AdministrationRoute("https://nap.nprod.net/administration/intramuscular", "Intramuscular")
        val pharmacyResults = (1..5).map { Pharmacy("https://nap.nprod.net/pharmacy/$it") }
        
        val viewData = AdministrationRouteViewData("intramuscular", route, pharmacyResults)
        
        assertEquals(5, viewData.pharmacyResults.size)
        pharmacyResults.forEachIndexed { index, pharmacy ->
            assertEquals(pharmacy, viewData.pharmacyResults[index])
        }
    }
}