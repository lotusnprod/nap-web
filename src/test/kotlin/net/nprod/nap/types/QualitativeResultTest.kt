package net.nprod.nap.types

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.assertNotEquals
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString

class QualitativeResultTest {
    
    @Test
    fun testQualitativeResultCreation() {
        val result = QualitativeResult(
            uri = "https://nap.nprod.net/result/positive",
            name = "Positive"
        )
        
        assertEquals("https://nap.nprod.net/result/positive", result.uri)
        assertEquals("Positive", result.name)
    }
    
    @Test
    fun testQualitativeResultEquality() {
        val result1 = QualitativeResult(
            uri = "https://nap.nprod.net/result/negative",
            name = "Negative"
        )
        val result2 = QualitativeResult(
            uri = "https://nap.nprod.net/result/negative",
            name = "Negative"
        )
        val result3 = QualitativeResult(
            uri = "https://nap.nprod.net/result/inconclusive",
            name = "Inconclusive"
        )
        
        assertEquals(result1, result2)
        assertNotEquals(result1, result3)
    }
    
    @Test
    fun testQualitativeResultCopy() {
        val result = QualitativeResult(
            uri = "https://nap.nprod.net/result/active",
            name = "Active"
        )
        
        val copy = result.copy(name = "Highly Active")
        
        assertEquals(result.uri, copy.uri)
        assertEquals("Highly Active", copy.name)
        assertNotEquals(result, copy)
    }
    
    @Test
    fun testQualitativeResultToString() {
        val result = QualitativeResult(
            uri = "https://nap.nprod.net/result/inactive",
            name = "Inactive"
        )
        
        val string = result.toString()
        assertTrue(string.contains("QualitativeResult"))
        assertTrue(string.contains("uri=https://nap.nprod.net/result/inactive"))
        assertTrue(string.contains("name=Inactive"))
    }
    
    @Test
    fun testQualitativeResultSerialization() {
        val result = QualitativeResult(
            uri = "https://nap.nprod.net/result/moderate",
            name = "Moderate"
        )
        
        // Test serialization
        val json = Json.encodeToString(result)
        assertTrue(json.contains("\"uri\":\"https://nap.nprod.net/result/moderate\""))
        assertTrue(json.contains("\"name\":\"Moderate\""))
        
        // Test deserialization
        val decoded = Json.decodeFromString<QualitativeResult>(json)
        assertEquals(result.uri, decoded.uri)
        assertEquals(result.name, decoded.name)
        assertEquals(result, decoded)
    }
    
    @Test
    fun testQualitativeResultHashCode() {
        val result1 = QualitativeResult(
            uri = "https://nap.nprod.net/result/weak",
            name = "Weak"
        )
        val result2 = QualitativeResult(
            uri = "https://nap.nprod.net/result/weak",
            name = "Weak"
        )
        
        assertEquals(result1.hashCode(), result2.hashCode())
    }
    
    @Test
    fun testQualitativeResultVariousTypes() {
        val results = listOf(
            QualitativeResult("https://nap.nprod.net/result/positive", "Positive"),
            QualitativeResult("https://nap.nprod.net/result/negative", "Negative"),
            QualitativeResult("https://nap.nprod.net/result/active", "Active"),
            QualitativeResult("https://nap.nprod.net/result/inactive", "Inactive"),
            QualitativeResult("https://nap.nprod.net/result/toxic", "Toxic"),
            QualitativeResult("https://nap.nprod.net/result/non-toxic", "Non-toxic")
        )
        
        assertEquals(6, results.size)
        assertEquals(6, results.distinct().size)
        assertTrue(results.all { it.uri.startsWith("https://nap.nprod.net/result/") })
    }
}