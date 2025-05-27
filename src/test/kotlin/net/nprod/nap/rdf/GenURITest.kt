package net.nprod.nap.rdf

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import genURI
import genNode

class GenURITest {
    
    @Test
    fun testGenURIWithTypeAndId() {
        val uri1 = genURI("compound", "123")
        assertEquals("https://nap.nprod.net/compound/123", uri1)
        
        val uri2 = genURI("organism", "456")
        assertEquals("https://nap.nprod.net/organism/456", uri2)
        
        val uri3 = genURI("pharmacy", "experiment-001")
        assertEquals("https://nap.nprod.net/pharmacy/experiment-001", uri3)
    }
    
    @Test
    fun testGenURIWithTypeAndIdContainingSlash() {
        assertFailsWith<IllegalArgumentException>("Type and id should not contain /") {
            genURI("compound/subtype", "123")
        }
        
        assertFailsWith<IllegalArgumentException>("Type and id should not contain /") {
            genURI("compound", "123/456")
        }
        
        assertFailsWith<IllegalArgumentException>("Type and id should not contain /") {
            genURI("type/with/slash", "id/with/slash")
        }
    }
    
    @Test
    fun testGenURIWithSingleObject() {
        val uri1 = genURI("compounds")
        assertEquals("https://nap.nprod.net/compounds", uri1)
        
        val uri2 = genURI("organisms")
        assertEquals("https://nap.nprod.net/organisms", uri2)
        
        val uri3 = genURI("pharmacies")
        assertEquals("https://nap.nprod.net/pharmacies", uri3)
    }
    
    @Test
    fun testGenURIWithObjectContainingSlash() {
        assertFailsWith<IllegalArgumentException>("Object should not contain /") {
            genURI("object/with/slash")
        }
        
        assertFailsWith<IllegalArgumentException>("Object should not contain /") {
            genURI("path/segment")
        }
    }
    
    @Test
    fun testGenURIWithEmptyStrings() {
        // Empty type and id
        val uri1 = genURI("", "")
        assertEquals("https://nap.nprod.net//", uri1)
        
        // Empty object
        val uri2 = genURI("")
        assertEquals("https://nap.nprod.net/", uri2)
    }
    
    @Test
    fun testGenURIWithSpecialCharacters() {
        // Test with special characters that are not slashes
        val uri1 = genURI("compound", "123-ABC")
        assertEquals("https://nap.nprod.net/compound/123-ABC", uri1)
        
        val uri2 = genURI("organism", "species_name")
        assertEquals("https://nap.nprod.net/organism/species_name", uri2)
        
        val uri3 = genURI("data-type")
        assertEquals("https://nap.nprod.net/data-type", uri3)
    }
    
    @Test
    fun testGenNode() {
        val node1 = genNode("compounds")
        assertNotNull(node1)
        assertEquals("https://nap.nprod.net/compounds", node1.uri)
        
        val node2 = genNode("organisms")
        assertNotNull(node2)
        assertEquals("https://nap.nprod.net/organisms", node2.uri)
    }
    
    @Test
    fun testGenNodeWithSlash() {
        assertFailsWith<IllegalArgumentException>("Object should not contain /") {
            genNode("object/with/slash")
        }
    }
    
    @Test
    fun testGenNodeWithEmptyString() {
        val node = genNode("")
        assertNotNull(node)
        assertEquals("https://nap.nprod.net/", node.uri)
    }
}