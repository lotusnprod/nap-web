package net.nprod.nap.types

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.assertNotEquals
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString

class GenderTest {
    
    @Test
    fun testGenderCreation() {
        val gender = Gender(
            uri = "https://nap.nprod.net/gender/male",
            code = "M",
            name = "Male"
        )
        
        assertEquals("https://nap.nprod.net/gender/male", gender.uri)
        assertEquals("M", gender.code)
        assertEquals("Male", gender.name)
    }
    
    @Test
    fun testGenderEquality() {
        val gender1 = Gender(
            uri = "https://nap.nprod.net/gender/female",
            code = "F",
            name = "Female"
        )
        val gender2 = Gender(
            uri = "https://nap.nprod.net/gender/female",
            code = "F",
            name = "Female"
        )
        val gender3 = Gender(
            uri = "https://nap.nprod.net/gender/other",
            code = "O",
            name = "Other"
        )
        
        assertEquals(gender1, gender2)
        assertNotEquals(gender1, gender3)
    }
    
    @Test
    fun testGenderCopy() {
        val gender = Gender(
            uri = "https://nap.nprod.net/gender/unknown",
            code = "U",
            name = "Unknown"
        )
        
        val copy = gender.copy(name = "Not Specified")
        
        assertEquals(gender.uri, copy.uri)
        assertEquals(gender.code, copy.code)
        assertEquals("Not Specified", copy.name)
        assertNotEquals(gender, copy)
    }
    
    @Test
    fun testGenderToString() {
        val gender = Gender(
            uri = "https://nap.nprod.net/gender/male",
            code = "M",
            name = "Male"
        )
        
        val string = gender.toString()
        assertTrue(string.contains("Gender"))
        assertTrue(string.contains("uri=https://nap.nprod.net/gender/male"))
        assertTrue(string.contains("code=M"))
        assertTrue(string.contains("name=Male"))
    }
    
    @Test
    fun testGenderSerialization() {
        val gender = Gender(
            uri = "https://nap.nprod.net/gender/female",
            code = "F",
            name = "Female"
        )
        
        // Test serialization
        val json = Json.encodeToString(gender)
        assertTrue(json.contains("\"uri\":\"https://nap.nprod.net/gender/female\""))
        assertTrue(json.contains("\"code\":\"F\""))
        assertTrue(json.contains("\"name\":\"Female\""))
        
        // Test deserialization
        val decoded = Json.decodeFromString<Gender>(json)
        assertEquals(gender.uri, decoded.uri)
        assertEquals(gender.code, decoded.code)
        assertEquals(gender.name, decoded.name)
        assertEquals(gender, decoded)
    }
    
    @Test
    fun testGenderHashCode() {
        val gender1 = Gender(
            uri = "https://nap.nprod.net/gender/male",
            code = "M",
            name = "Male"
        )
        val gender2 = Gender(
            uri = "https://nap.nprod.net/gender/male",
            code = "M",
            name = "Male"
        )
        
        assertEquals(gender1.hashCode(), gender2.hashCode())
    }
    
    @Test
    fun testGenderWithDifferentCodes() {
        val gender1 = Gender("https://nap.nprod.net/gender/1", "M", "Male")
        val gender2 = Gender("https://nap.nprod.net/gender/2", "F", "Female")
        val gender3 = Gender("https://nap.nprod.net/gender/3", "X", "Non-binary")
        
        assertNotEquals(gender1.code, gender2.code)
        assertNotEquals(gender2.code, gender3.code)
        assertNotEquals(gender1, gender2)
        assertNotEquals(gender2, gender3)
    }
}