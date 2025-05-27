package net.nprod.nap.types

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.assertNotEquals
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString

class WorktypeGroupTest {
    
    @Test
    fun testWorktypeGroupCreation() {
        val group = WorktypeGroup(
            uri = "https://nap.nprod.net/worktypegroup/isolation",
            code = "ISO",
            name = "Isolation Methods"
        )
        
        assertEquals("https://nap.nprod.net/worktypegroup/isolation", group.uri)
        assertEquals("ISO", group.code)
        assertEquals("Isolation Methods", group.name)
    }
    
    @Test
    fun testWorktypeGroupEquality() {
        val group1 = WorktypeGroup(
            uri = "https://nap.nprod.net/worktypegroup/extraction",
            code = "EXT",
            name = "Extraction Methods"
        )
        val group2 = WorktypeGroup(
            uri = "https://nap.nprod.net/worktypegroup/extraction",
            code = "EXT",
            name = "Extraction Methods"
        )
        val group3 = WorktypeGroup(
            uri = "https://nap.nprod.net/worktypegroup/purification",
            code = "PUR",
            name = "Purification Methods"
        )
        
        assertEquals(group1, group2)
        assertNotEquals(group1, group3)
    }
    
    @Test
    fun testWorktypeGroupCopy() {
        val group = WorktypeGroup(
            uri = "https://nap.nprod.net/worktypegroup/analysis",
            code = "ANA",
            name = "Analysis Methods"
        )
        
        val copy = group.copy(name = "Analytical Methods")
        
        assertEquals(group.uri, copy.uri)
        assertEquals(group.code, copy.code)
        assertEquals("Analytical Methods", copy.name)
        assertNotEquals(group, copy)
    }
    
    @Test
    fun testWorktypeGroupToString() {
        val group = WorktypeGroup(
            uri = "https://nap.nprod.net/worktypegroup/synthesis",
            code = "SYN",
            name = "Synthesis Methods"
        )
        
        val string = group.toString()
        assertTrue(string.contains("WorktypeGroup"))
        assertTrue(string.contains("uri=https://nap.nprod.net/worktypegroup/synthesis"))
        assertTrue(string.contains("code=SYN"))
        assertTrue(string.contains("name=Synthesis Methods"))
    }
    
    @Test
    fun testWorktypeGroupSerialization() {
        val group = WorktypeGroup(
            uri = "https://nap.nprod.net/worktypegroup/characterization",
            code = "CHAR",
            name = "Characterization Methods"
        )
        
        // Test serialization
        val json = Json.encodeToString(group)
        assertTrue(json.contains("\"uri\":\"https://nap.nprod.net/worktypegroup/characterization\""))
        assertTrue(json.contains("\"code\":\"CHAR\""))
        assertTrue(json.contains("\"name\":\"Characterization Methods\""))
        
        // Test deserialization
        val decoded = Json.decodeFromString<WorktypeGroup>(json)
        assertEquals(group.uri, decoded.uri)
        assertEquals(group.code, decoded.code)
        assertEquals(group.name, decoded.name)
        assertEquals(group, decoded)
    }
    
    @Test
    fun testWorktypeGroupHashCode() {
        val group1 = WorktypeGroup(
            uri = "https://nap.nprod.net/worktypegroup/bioassay",
            code = "BIO",
            name = "Bioassay Methods"
        )
        val group2 = WorktypeGroup(
            uri = "https://nap.nprod.net/worktypegroup/bioassay",
            code = "BIO",
            name = "Bioassay Methods"
        )
        
        assertEquals(group1.hashCode(), group2.hashCode())
    }
    
    @Test
    fun testWorktypeGroupVariousTypes() {
        val groups = listOf(
            WorktypeGroup("https://nap.nprod.net/worktypegroup/1", "ISO", "Isolation"),
            WorktypeGroup("https://nap.nprod.net/worktypegroup/2", "EXT", "Extraction"),
            WorktypeGroup("https://nap.nprod.net/worktypegroup/3", "PUR", "Purification"),
            WorktypeGroup("https://nap.nprod.net/worktypegroup/4", "CHAR", "Characterization"),
            WorktypeGroup("https://nap.nprod.net/worktypegroup/5", "TEST", "Testing"),
            WorktypeGroup("https://nap.nprod.net/worktypegroup/6", "PROC", "Processing")
        )
        
        assertEquals(6, groups.size)
        assertEquals(6, groups.map { it.code }.distinct().size)
        assertTrue(groups.all { it.uri.contains("worktypegroup") })
        assertNotEquals(groups[0].code, groups[1].code)
    }
}