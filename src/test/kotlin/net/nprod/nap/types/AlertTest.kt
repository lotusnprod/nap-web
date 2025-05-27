package net.nprod.nap.types

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.assertNotEquals
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString

class AlertTest {
    
    @Test
    fun testAlertCreation() {
        val alert = Alert(
            uri = "https://nap.nprod.net/alert/warning",
            name = "Warning"
        )
        
        assertEquals("https://nap.nprod.net/alert/warning", alert.uri)
        assertEquals("Warning", alert.name)
    }
    
    @Test
    fun testAlertEquality() {
        val alert1 = Alert(
            uri = "https://nap.nprod.net/alert/caution",
            name = "Caution"
        )
        val alert2 = Alert(
            uri = "https://nap.nprod.net/alert/caution",
            name = "Caution"
        )
        val alert3 = Alert(
            uri = "https://nap.nprod.net/alert/danger",
            name = "Danger"
        )
        
        assertEquals(alert1, alert2)
        assertNotEquals(alert1, alert3)
    }
    
    @Test
    fun testAlertCopy() {
        val alert = Alert(
            uri = "https://nap.nprod.net/alert/notice",
            name = "Notice"
        )
        
        val copy = alert.copy(name = "Important Notice")
        
        assertEquals(alert.uri, copy.uri)
        assertEquals("Important Notice", copy.name)
        assertNotEquals(alert, copy)
    }
    
    @Test
    fun testAlertToString() {
        val alert = Alert(
            uri = "https://nap.nprod.net/alert/info",
            name = "Information"
        )
        
        val string = alert.toString()
        assertTrue(string.contains("Alert"))
        assertTrue(string.contains("uri=https://nap.nprod.net/alert/info"))
        assertTrue(string.contains("name=Information"))
    }
    
    @Test
    fun testAlertSerialization() {
        val alert = Alert(
            uri = "https://nap.nprod.net/alert/critical",
            name = "Critical"
        )
        
        // Test serialization
        val json = Json.encodeToString(alert)
        assertTrue(json.contains("\"uri\":\"https://nap.nprod.net/alert/critical\""))
        assertTrue(json.contains("\"name\":\"Critical\""))
        
        // Test deserialization
        val decoded = Json.decodeFromString<Alert>(json)
        assertEquals(alert.uri, decoded.uri)
        assertEquals(alert.name, decoded.name)
        assertEquals(alert, decoded)
    }
    
    @Test
    fun testAlertHashCode() {
        val alert1 = Alert(
            uri = "https://nap.nprod.net/alert/error",
            name = "Error"
        )
        val alert2 = Alert(
            uri = "https://nap.nprod.net/alert/error",
            name = "Error"
        )
        
        assertEquals(alert1.hashCode(), alert2.hashCode())
    }
}