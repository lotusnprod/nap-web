package net.nprod.nap.types

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.assertNotEquals
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString

class ExpressionResultTest {
    
    @Test
    fun testExpressionResultCreation() {
        val result = ExpressionResult(
            uri = "https://nap.nprod.net/expressionresult/high",
            code = "H",
            name = "High Expression"
        )
        
        assertEquals("https://nap.nprod.net/expressionresult/high", result.uri)
        assertEquals("H", result.code)
        assertEquals("High Expression", result.name)
    }
    
    @Test
    fun testExpressionResultEquality() {
        val result1 = ExpressionResult(
            uri = "https://nap.nprod.net/expressionresult/low",
            code = "L",
            name = "Low Expression"
        )
        val result2 = ExpressionResult(
            uri = "https://nap.nprod.net/expressionresult/low",
            code = "L",
            name = "Low Expression"
        )
        val result3 = ExpressionResult(
            uri = "https://nap.nprod.net/expressionresult/medium",
            code = "M",
            name = "Medium Expression"
        )
        
        assertEquals(result1, result2)
        assertNotEquals(result1, result3)
    }
    
    @Test
    fun testExpressionResultCopy() {
        val result = ExpressionResult(
            uri = "https://nap.nprod.net/expressionresult/none",
            code = "N",
            name = "No Expression"
        )
        
        val copy = result.copy(name = "Not Expressed")
        
        assertEquals(result.uri, copy.uri)
        assertEquals(result.code, copy.code)
        assertEquals("Not Expressed", copy.name)
        assertNotEquals(result, copy)
    }
    
    @Test
    fun testExpressionResultToString() {
        val result = ExpressionResult(
            uri = "https://nap.nprod.net/expressionresult/very-high",
            code = "VH",
            name = "Very High Expression"
        )
        
        val string = result.toString()
        assertTrue(string.contains("ExpressionResult"))
        assertTrue(string.contains("uri=https://nap.nprod.net/expressionresult/very-high"))
        assertTrue(string.contains("code=VH"))
        assertTrue(string.contains("name=Very High Expression"))
    }
    
    @Test
    fun testExpressionResultSerialization() {
        val result = ExpressionResult(
            uri = "https://nap.nprod.net/expressionresult/variable",
            code = "V",
            name = "Variable Expression"
        )
        
        // Test serialization
        val json = Json.encodeToString(result)
        assertTrue(json.contains("\"uri\":\"https://nap.nprod.net/expressionresult/variable\""))
        assertTrue(json.contains("\"code\":\"V\""))
        assertTrue(json.contains("\"name\":\"Variable Expression\""))
        
        // Test deserialization
        val decoded = Json.decodeFromString<ExpressionResult>(json)
        assertEquals(result.uri, decoded.uri)
        assertEquals(result.code, decoded.code)
        assertEquals(result.name, decoded.name)
        assertEquals(result, decoded)
    }
    
    @Test
    fun testExpressionResultHashCode() {
        val result1 = ExpressionResult(
            uri = "https://nap.nprod.net/expressionresult/positive",
            code = "P",
            name = "Positive"
        )
        val result2 = ExpressionResult(
            uri = "https://nap.nprod.net/expressionresult/positive",
            code = "P",
            name = "Positive"
        )
        
        assertEquals(result1.hashCode(), result2.hashCode())
    }
    
    @Test
    fun testExpressionResultDifferentCodes() {
        val results = listOf(
            ExpressionResult("https://nap.nprod.net/expressionresult/1", "++", "Strong Positive"),
            ExpressionResult("https://nap.nprod.net/expressionresult/2", "+", "Positive"),
            ExpressionResult("https://nap.nprod.net/expressionresult/3", "-", "Negative"),
            ExpressionResult("https://nap.nprod.net/expressionresult/4", "--", "Strong Negative"),
            ExpressionResult("https://nap.nprod.net/expressionresult/5", "+/-", "Mixed")
        )
        
        assertEquals(5, results.size)
        assertEquals(5, results.map { it.code }.distinct().size)
        assertNotEquals(results[0].code, results[1].code)
    }
}