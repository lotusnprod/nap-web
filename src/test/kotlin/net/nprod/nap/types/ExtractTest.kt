package net.nprod.nap.types

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.assertNotEquals
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString

class ExtractTest {
    
    @Test
    fun testExtractCreation() {
        val extract = Extract(
            uri = "https://nap.nprod.net/extract/aqueous",
            code = "AQ",
            name = "Aqueous Extract"
        )
        
        assertEquals("https://nap.nprod.net/extract/aqueous", extract.uri)
        assertEquals("AQ", extract.code)
        assertEquals("Aqueous Extract", extract.name)
    }
    
    @Test
    fun testExtractEquality() {
        val extract1 = Extract(
            uri = "https://nap.nprod.net/extract/ethanolic",
            code = "ETH",
            name = "Ethanolic Extract"
        )
        val extract2 = Extract(
            uri = "https://nap.nprod.net/extract/ethanolic",
            code = "ETH",
            name = "Ethanolic Extract"
        )
        val extract3 = Extract(
            uri = "https://nap.nprod.net/extract/methanolic",
            code = "METH",
            name = "Methanolic Extract"
        )
        
        assertEquals(extract1, extract2)
        assertNotEquals(extract1, extract3)
    }
    
    @Test
    fun testExtractCopy() {
        val extract = Extract(
            uri = "https://nap.nprod.net/extract/chloroform",
            code = "CHCl3",
            name = "Chloroform Extract"
        )
        
        val copy = extract.copy(name = "Chloroform Soluble Extract")
        
        assertEquals(extract.uri, copy.uri)
        assertEquals(extract.code, copy.code)
        assertEquals("Chloroform Soluble Extract", copy.name)
        assertNotEquals(extract, copy)
    }
    
    @Test
    fun testExtractToString() {
        val extract = Extract(
            uri = "https://nap.nprod.net/extract/hexane",
            code = "HEX",
            name = "Hexane Extract"
        )
        
        val string = extract.toString()
        assertTrue(string.contains("Extract"))
        assertTrue(string.contains("uri=https://nap.nprod.net/extract/hexane"))
        assertTrue(string.contains("code=HEX"))
        assertTrue(string.contains("name=Hexane Extract"))
    }
    
    @Test
    fun testExtractSerialization() {
        val extract = Extract(
            uri = "https://nap.nprod.net/extract/acetone",
            code = "ACE",
            name = "Acetone Extract"
        )
        
        // Test serialization
        val json = Json.encodeToString(extract)
        assertTrue(json.contains("\"uri\":\"https://nap.nprod.net/extract/acetone\""))
        assertTrue(json.contains("\"code\":\"ACE\""))
        assertTrue(json.contains("\"name\":\"Acetone Extract\""))
        
        // Test deserialization
        val decoded = Json.decodeFromString<Extract>(json)
        assertEquals(extract.uri, decoded.uri)
        assertEquals(extract.code, decoded.code)
        assertEquals(extract.name, decoded.name)
        assertEquals(extract, decoded)
    }
    
    @Test
    fun testExtractHashCode() {
        val extract1 = Extract(
            uri = "https://nap.nprod.net/extract/dichloromethane",
            code = "DCM",
            name = "Dichloromethane Extract"
        )
        val extract2 = Extract(
            uri = "https://nap.nprod.net/extract/dichloromethane",
            code = "DCM",
            name = "Dichloromethane Extract"
        )
        
        assertEquals(extract1.hashCode(), extract2.hashCode())
    }
    
    @Test
    fun testExtractVariousTypes() {
        val extracts = listOf(
            Extract("https://nap.nprod.net/extract/1", "AQ", "Aqueous"),
            Extract("https://nap.nprod.net/extract/2", "ETH", "Ethanolic"),
            Extract("https://nap.nprod.net/extract/3", "METH", "Methanolic"),
            Extract("https://nap.nprod.net/extract/4", "HEX", "Hexane"),
            Extract("https://nap.nprod.net/extract/5", "CHCl3", "Chloroform"),
            Extract("https://nap.nprod.net/extract/6", "EtOAc", "Ethyl Acetate"),
            Extract("https://nap.nprod.net/extract/7", "BuOH", "Butanol")
        )
        
        assertEquals(7, extracts.size)
        assertEquals(7, extracts.map { it.code }.distinct().size)
        assertTrue(extracts.all { it.uri.contains("extract") })
        assertNotEquals(extracts[0].code, extracts[1].code)
    }
}