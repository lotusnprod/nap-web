package net.nprod.nap.types

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.assertNotEquals
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString

class AdministrationRouteTest {
    
    @Test
    fun testAdministrationRouteCreation() {
        val route = AdministrationRoute(
            uri = "https://nap.nprod.net/administration/oral",
            name = "Oral"
        )
        
        assertEquals("https://nap.nprod.net/administration/oral", route.uri)
        assertEquals("Oral", route.name)
    }
    
    @Test
    fun testAdministrationRouteEquality() {
        val route1 = AdministrationRoute(
            uri = "https://nap.nprod.net/administration/topical",
            name = "Topical"
        )
        val route2 = AdministrationRoute(
            uri = "https://nap.nprod.net/administration/topical",
            name = "Topical"
        )
        val route3 = AdministrationRoute(
            uri = "https://nap.nprod.net/administration/injection",
            name = "Injection"
        )
        
        assertEquals(route1, route2)
        assertNotEquals(route1, route3)
    }
    
    @Test
    fun testAdministrationRouteCopy() {
        val route = AdministrationRoute(
            uri = "https://nap.nprod.net/administration/inhalation",
            name = "Inhalation"
        )
        
        val copy = route.copy(name = "Modified Inhalation")
        
        assertEquals(route.uri, copy.uri)
        assertEquals("Modified Inhalation", copy.name)
        assertNotEquals(route, copy)
    }
    
    @Test
    fun testAdministrationRouteToString() {
        val route = AdministrationRoute(
            uri = "https://nap.nprod.net/administration/sublingual",
            name = "Sublingual"
        )
        
        val string = route.toString()
        assertTrue(string.contains("AdministrationRoute"))
        assertTrue(string.contains("uri=https://nap.nprod.net/administration/sublingual"))
        assertTrue(string.contains("name=Sublingual"))
    }
    
    @Test
    fun testAdministrationRouteSerialization() {
        val route = AdministrationRoute(
            uri = "https://nap.nprod.net/administration/transdermal",
            name = "Transdermal"
        )
        
        // Test serialization
        val json = Json.encodeToString(route)
        assertTrue(json.contains("\"uri\":\"https://nap.nprod.net/administration/transdermal\""))
        assertTrue(json.contains("\"name\":\"Transdermal\""))
        
        // Test deserialization
        val decoded = Json.decodeFromString<AdministrationRoute>(json)
        assertEquals(route.uri, decoded.uri)
        assertEquals(route.name, decoded.name)
        assertEquals(route, decoded)
    }
    
    @Test
    fun testAdministrationRouteHashCode() {
        val route1 = AdministrationRoute(
            uri = "https://nap.nprod.net/administration/ocular",
            name = "Ocular"
        )
        val route2 = AdministrationRoute(
            uri = "https://nap.nprod.net/administration/ocular",
            name = "Ocular"
        )
        
        assertEquals(route1.hashCode(), route2.hashCode())
    }
}