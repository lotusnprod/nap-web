package net.nprod.nap.types

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.assertNotEquals
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString

class CompoundCodeTest {
    
    @Test
    fun testCompoundCodeCreation() {
        val code = CompoundCode(
            uri = "https://nap.nprod.net/code/CAS123456",
            name = "CAS123456"
        )
        
        assertEquals("https://nap.nprod.net/code/CAS123456", code.uri)
        assertEquals("CAS123456", code.name)
    }
    
    @Test
    fun testCompoundCodeEquality() {
        val code1 = CompoundCode(
            uri = "https://nap.nprod.net/code/PubChem789",
            name = "PubChem789"
        )
        val code2 = CompoundCode(
            uri = "https://nap.nprod.net/code/PubChem789",
            name = "PubChem789"
        )
        val code3 = CompoundCode(
            uri = "https://nap.nprod.net/code/ChemSpider111",
            name = "ChemSpider111"
        )
        
        assertEquals(code1, code2)
        assertNotEquals(code1, code3)
    }
    
    @Test
    fun testCompoundCodeCopy() {
        val code = CompoundCode(
            uri = "https://nap.nprod.net/code/SMILES123",
            name = "SMILES123"
        )
        
        val copy = code.copy(name = "Modified SMILES")
        
        assertEquals(code.uri, copy.uri)
        assertEquals("Modified SMILES", copy.name)
        assertNotEquals(code, copy)
    }
    
    @Test
    fun testCompoundCodeToString() {
        val code = CompoundCode(
            uri = "https://nap.nprod.net/code/InChI999",
            name = "InChI999"
        )
        
        val string = code.toString()
        assertTrue(string.contains("CompoundCode"))
        assertTrue(string.contains("uri=https://nap.nprod.net/code/InChI999"))
        assertTrue(string.contains("name=InChI999"))
    }
    
    @Test
    fun testCompoundCodeSerialization() {
        val code = CompoundCode(
            uri = "https://nap.nprod.net/code/KEGG555",
            name = "KEGG555"
        )
        
        // Test serialization
        val json = Json.encodeToString(code)
        assertTrue(json.contains("\"uri\":\"https://nap.nprod.net/code/KEGG555\""))
        assertTrue(json.contains("\"name\":\"KEGG555\""))
        
        // Test deserialization
        val decoded = Json.decodeFromString<CompoundCode>(json)
        assertEquals(code.uri, decoded.uri)
        assertEquals(code.name, decoded.name)
        assertEquals(code, decoded)
    }
    
    @Test
    fun testCompoundCodeHashCode() {
        val code1 = CompoundCode(
            uri = "https://nap.nprod.net/code/HMDB777",
            name = "HMDB777"
        )
        val code2 = CompoundCode(
            uri = "https://nap.nprod.net/code/HMDB777",
            name = "HMDB777"
        )
        
        assertEquals(code1.hashCode(), code2.hashCode())
    }
    
    @Test
    fun testCompoundCodeMutability() {
        val code = CompoundCode(
            uri = "https://nap.nprod.net/code/Original",
            name = "Original"
        )
        
        // Test that properties are mutable (var)
        code.uri = "https://nap.nprod.net/code/Modified"
        code.name = "Modified"
        
        assertEquals("https://nap.nprod.net/code/Modified", code.uri)
        assertEquals("Modified", code.name)
    }
}