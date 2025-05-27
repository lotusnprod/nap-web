package net.nprod.nap.types

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.assertNotEquals
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString

class DoseExpressionTest {
    
    @Test
    fun testDoseExpressionCreation() {
        val expression = DoseExpression(
            uri = "https://nap.nprod.net/doseexpression/ld50",
            name = "LD50"
        )
        
        assertEquals("https://nap.nprod.net/doseexpression/ld50", expression.uri)
        assertEquals("LD50", expression.name)
    }
    
    @Test
    fun testDoseExpressionEquality() {
        val expr1 = DoseExpression(
            uri = "https://nap.nprod.net/doseexpression/ed50",
            name = "ED50"
        )
        val expr2 = DoseExpression(
            uri = "https://nap.nprod.net/doseexpression/ed50",
            name = "ED50"
        )
        val expr3 = DoseExpression(
            uri = "https://nap.nprod.net/doseexpression/ic50",
            name = "IC50"
        )
        
        assertEquals(expr1, expr2)
        assertNotEquals(expr1, expr3)
    }
    
    @Test
    fun testDoseExpressionCopy() {
        val expression = DoseExpression(
            uri = "https://nap.nprod.net/doseexpression/mic",
            name = "MIC"
        )
        
        val copy = expression.copy(name = "Minimum Inhibitory Concentration")
        
        assertEquals(expression.uri, copy.uri)
        assertEquals("Minimum Inhibitory Concentration", copy.name)
        assertNotEquals(expression, copy)
    }
    
    @Test
    fun testDoseExpressionToString() {
        val expression = DoseExpression(
            uri = "https://nap.nprod.net/doseexpression/ec50",
            name = "EC50"
        )
        
        val string = expression.toString()
        assertTrue(string.contains("DoseExpression"))
        assertTrue(string.contains("uri=https://nap.nprod.net/doseexpression/ec50"))
        assertTrue(string.contains("name=EC50"))
    }
    
    @Test
    fun testDoseExpressionSerialization() {
        val expression = DoseExpression(
            uri = "https://nap.nprod.net/doseexpression/lc50",
            name = "LC50"
        )
        
        // Test serialization
        val json = Json.encodeToString(expression)
        assertTrue(json.contains("\"uri\":\"https://nap.nprod.net/doseexpression/lc50\""))
        assertTrue(json.contains("\"name\":\"LC50\""))
        
        // Test deserialization
        val decoded = Json.decodeFromString<DoseExpression>(json)
        assertEquals(expression.uri, decoded.uri)
        assertEquals(expression.name, decoded.name)
        assertEquals(expression, decoded)
    }
    
    @Test
    fun testDoseExpressionHashCode() {
        val expr1 = DoseExpression(
            uri = "https://nap.nprod.net/doseexpression/noael",
            name = "NOAEL"
        )
        val expr2 = DoseExpression(
            uri = "https://nap.nprod.net/doseexpression/noael",
            name = "NOAEL"
        )
        
        assertEquals(expr1.hashCode(), expr2.hashCode())
    }
    
    @Test
    fun testDoseExpressionVariousTypes() {
        val expressions = listOf(
            DoseExpression("https://nap.nprod.net/doseexpression/ld50", "LD50"),
            DoseExpression("https://nap.nprod.net/doseexpression/ed50", "ED50"),
            DoseExpression("https://nap.nprod.net/doseexpression/ic50", "IC50"),
            DoseExpression("https://nap.nprod.net/doseexpression/ec50", "EC50"),
            DoseExpression("https://nap.nprod.net/doseexpression/mic", "MIC"),
            DoseExpression("https://nap.nprod.net/doseexpression/mac", "MAC")
        )
        
        assertEquals(6, expressions.size)
        assertEquals(6, expressions.distinct().size)
        assertTrue(expressions.all { it.uri.startsWith("https://nap.nprod.net/doseexpression/") })
    }
}