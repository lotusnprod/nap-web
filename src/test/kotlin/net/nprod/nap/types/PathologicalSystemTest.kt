package net.nprod.nap.types

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.assertNotEquals
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString

class PathologicalSystemTest {
    
    @Test
    fun testPathologicalSystemCreation() {
        val system = PathologicalSystem(
            uri = "https://nap.nprod.net/pathsystem/cardiovascular",
            code = "CVS",
            name = "Cardiovascular System"
        )
        
        assertEquals("https://nap.nprod.net/pathsystem/cardiovascular", system.uri)
        assertEquals("CVS", system.code)
        assertEquals("Cardiovascular System", system.name)
    }
    
    @Test
    fun testPathologicalSystemEquality() {
        val system1 = PathologicalSystem(
            uri = "https://nap.nprod.net/pathsystem/respiratory",
            code = "RESP",
            name = "Respiratory System"
        )
        val system2 = PathologicalSystem(
            uri = "https://nap.nprod.net/pathsystem/respiratory",
            code = "RESP",
            name = "Respiratory System"
        )
        val system3 = PathologicalSystem(
            uri = "https://nap.nprod.net/pathsystem/nervous",
            code = "CNS",
            name = "Central Nervous System"
        )
        
        assertEquals(system1, system2)
        assertNotEquals(system1, system3)
    }
    
    @Test
    fun testPathologicalSystemCopy() {
        val system = PathologicalSystem(
            uri = "https://nap.nprod.net/pathsystem/digestive",
            code = "GI",
            name = "Gastrointestinal System"
        )
        
        val copy = system.copy(name = "Digestive System")
        
        assertEquals(system.uri, copy.uri)
        assertEquals(system.code, copy.code)
        assertEquals("Digestive System", copy.name)
        assertNotEquals(system, copy)
    }
    
    @Test
    fun testPathologicalSystemToString() {
        val system = PathologicalSystem(
            uri = "https://nap.nprod.net/pathsystem/immune",
            code = "IMM",
            name = "Immune System"
        )
        
        val string = system.toString()
        assertTrue(string.contains("PathologicalSystem"))
        assertTrue(string.contains("uri=https://nap.nprod.net/pathsystem/immune"))
        assertTrue(string.contains("code=IMM"))
        assertTrue(string.contains("name=Immune System"))
    }
    
    @Test
    fun testPathologicalSystemSerialization() {
        val system = PathologicalSystem(
            uri = "https://nap.nprod.net/pathsystem/endocrine",
            code = "ENDO",
            name = "Endocrine System"
        )
        
        // Test serialization
        val json = Json.encodeToString(system)
        assertTrue(json.contains("\"uri\":\"https://nap.nprod.net/pathsystem/endocrine\""))
        assertTrue(json.contains("\"code\":\"ENDO\""))
        assertTrue(json.contains("\"name\":\"Endocrine System\""))
        
        // Test deserialization
        val decoded = Json.decodeFromString<PathologicalSystem>(json)
        assertEquals(system.uri, decoded.uri)
        assertEquals(system.code, decoded.code)
        assertEquals(system.name, decoded.name)
        assertEquals(system, decoded)
    }
    
    @Test
    fun testPathologicalSystemHashCode() {
        val system1 = PathologicalSystem(
            uri = "https://nap.nprod.net/pathsystem/renal",
            code = "REN",
            name = "Renal System"
        )
        val system2 = PathologicalSystem(
            uri = "https://nap.nprod.net/pathsystem/renal",
            code = "REN",
            name = "Renal System"
        )
        
        assertEquals(system1.hashCode(), system2.hashCode())
    }
    
    @Test
    fun testPathologicalSystemVariousSystems() {
        val systems = listOf(
            PathologicalSystem("https://nap.nprod.net/pathsystem/1", "CVS", "Cardiovascular"),
            PathologicalSystem("https://nap.nprod.net/pathsystem/2", "RESP", "Respiratory"),
            PathologicalSystem("https://nap.nprod.net/pathsystem/3", "CNS", "Central Nervous"),
            PathologicalSystem("https://nap.nprod.net/pathsystem/4", "GI", "Gastrointestinal"),
            PathologicalSystem("https://nap.nprod.net/pathsystem/5", "MSK", "Musculoskeletal"),
            PathologicalSystem("https://nap.nprod.net/pathsystem/6", "DERM", "Dermatological")
        )
        
        assertEquals(6, systems.size)
        assertEquals(6, systems.map { it.code }.distinct().size)
        assertTrue(systems.all { it.uri.contains("pathsystem") })
        assertNotEquals(systems[0].code, systems[1].code)
    }
}