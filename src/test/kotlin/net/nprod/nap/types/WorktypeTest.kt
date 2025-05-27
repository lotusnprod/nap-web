package net.nprod.nap.types

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.assertNotEquals
import kotlin.test.assertNull
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString

class WorktypeTest {
    
    @Test
    fun testWorktypeCreation() {
        val group = WorktypeGroup("https://nap.nprod.net/worktypegroup/isolation", "ISO", "Isolation")
        val worktype = Worktype(
            uri = "https://nap.nprod.net/worktype/chromatography",
            code = "CHROM",
            name = "Chromatography",
            group = group
        )
        
        assertEquals("https://nap.nprod.net/worktype/chromatography", worktype.uri)
        assertEquals("CHROM", worktype.code)
        assertEquals("Chromatography", worktype.name)
        assertEquals(group, worktype.group)
    }
    
    @Test
    fun testWorktypeWithoutGroup() {
        val worktype = Worktype(
            uri = "https://nap.nprod.net/worktype/extraction",
            code = "EXT",
            name = "Extraction"
        )
        
        assertEquals("https://nap.nprod.net/worktype/extraction", worktype.uri)
        assertEquals("EXT", worktype.code)
        assertEquals("Extraction", worktype.name)
        assertNull(worktype.group)
    }
    
    @Test
    fun testWorktypeEquality() {
        val group = WorktypeGroup("https://nap.nprod.net/worktypegroup/analysis", "ANA", "Analysis")
        val worktype1 = Worktype(
            uri = "https://nap.nprod.net/worktype/nmr",
            code = "NMR",
            name = "NMR Spectroscopy",
            group = group
        )
        val worktype2 = Worktype(
            uri = "https://nap.nprod.net/worktype/nmr",
            code = "NMR",
            name = "NMR Spectroscopy",
            group = group
        )
        val worktype3 = Worktype(
            uri = "https://nap.nprod.net/worktype/ms",
            code = "MS",
            name = "Mass Spectrometry",
            group = group
        )
        
        assertEquals(worktype1, worktype2)
        assertNotEquals(worktype1, worktype3)
    }
    
    @Test
    fun testWorktypeCopy() {
        val group = WorktypeGroup("https://nap.nprod.net/worktypegroup/separation", "SEP", "Separation")
        val worktype = Worktype(
            uri = "https://nap.nprod.net/worktype/hplc",
            code = "HPLC",
            name = "HPLC",
            group = group
        )
        
        val newGroup = WorktypeGroup("https://nap.nprod.net/worktypegroup/analysis", "ANA", "Analysis")
        val copy = worktype.copy(name = "High Performance Liquid Chromatography", group = newGroup)
        
        assertEquals(worktype.uri, copy.uri)
        assertEquals(worktype.code, copy.code)
        assertEquals("High Performance Liquid Chromatography", copy.name)
        assertEquals(newGroup, copy.group)
        assertNotEquals(worktype, copy)
    }
    
    @Test
    fun testWorktypeToString() {
        val worktype = Worktype(
            uri = "https://nap.nprod.net/worktype/tlc",
            code = "TLC",
            name = "Thin Layer Chromatography"
        )
        
        val string = worktype.toString()
        assertTrue(string.contains("Worktype"))
        assertTrue(string.contains("uri=https://nap.nprod.net/worktype/tlc"))
        assertTrue(string.contains("code=TLC"))
        assertTrue(string.contains("name=Thin Layer Chromatography"))
    }
    
    @Test
    fun testWorktypeSerialization() {
        val group = WorktypeGroup("https://nap.nprod.net/worktypegroup/spectroscopy", "SPEC", "Spectroscopy")
        val worktype = Worktype(
            uri = "https://nap.nprod.net/worktype/ir",
            code = "IR",
            name = "Infrared Spectroscopy",
            group = group
        )
        
        // Test serialization
        val json = Json.encodeToString(worktype)
        assertTrue(json.contains("\"uri\":\"https://nap.nprod.net/worktype/ir\""))
        assertTrue(json.contains("\"code\":\"IR\""))
        assertTrue(json.contains("\"name\":\"Infrared Spectroscopy\""))
        assertTrue(json.contains("\"group\""))
        
        // Test deserialization
        val decoded = Json.decodeFromString<Worktype>(json)
        assertEquals(worktype.uri, decoded.uri)
        assertEquals(worktype.code, decoded.code)
        assertEquals(worktype.name, decoded.name)
        assertEquals(worktype.group?.uri, decoded.group?.uri)
    }
    
    @Test
    fun testWorktypeHashCode() {
        val worktype1 = Worktype(
            uri = "https://nap.nprod.net/worktype/gc",
            code = "GC",
            name = "Gas Chromatography"
        )
        val worktype2 = Worktype(
            uri = "https://nap.nprod.net/worktype/gc",
            code = "GC",
            name = "Gas Chromatography"
        )
        
        assertEquals(worktype1.hashCode(), worktype2.hashCode())
    }
    
    @Test
    fun testWorktypeVariousTypes() {
        val isolationGroup = WorktypeGroup("https://nap.nprod.net/worktypegroup/isolation", "ISO", "Isolation")
        val analysisGroup = WorktypeGroup("https://nap.nprod.net/worktypegroup/analysis", "ANA", "Analysis")
        
        val worktypes = listOf(
            Worktype("https://nap.nprod.net/worktype/1", "HPLC", "HPLC", analysisGroup),
            Worktype("https://nap.nprod.net/worktype/2", "GC", "Gas Chromatography", analysisGroup),
            Worktype("https://nap.nprod.net/worktype/3", "NMR", "NMR", analysisGroup),
            Worktype("https://nap.nprod.net/worktype/4", "MS", "Mass Spectrometry", analysisGroup),
            Worktype("https://nap.nprod.net/worktype/5", "CC", "Column Chromatography", isolationGroup),
            Worktype("https://nap.nprod.net/worktype/6", "PREP", "Preparative HPLC", isolationGroup)
        )
        
        assertEquals(6, worktypes.size)
        assertEquals(6, worktypes.map { it.code }.distinct().size)
        assertTrue(worktypes.all { it.uri.contains("worktype") })
        assertEquals(2, worktypes.map { it.group }.distinct().size)
    }
}