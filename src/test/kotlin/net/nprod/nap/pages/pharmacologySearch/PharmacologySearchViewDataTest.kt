package net.nprod.nap.pages.pharmacologySearch

import kotlin.test.*

class PharmacologySearchViewDataTest {
    
    @Test
    fun testPharmacologySearchViewDataCreation() {
        val viewData = PharmacologySearchViewData()
        
        assertNull(viewData.query)
        assertTrue(viewData.pharmacologyEntries.isEmpty())
    }
    
    @Test
    fun testPharmacologySearchViewDataWithQuery() {
        val viewData = PharmacologySearchViewData(
            query = "antimicrobial"
        )
        
        assertEquals("antimicrobial", viewData.query)
        assertTrue(viewData.pharmacologyEntries.isEmpty())
    }
    
    @Test
    fun testPharmacologySearchViewDataWithEntries() {
        val entries = listOf(
            mapOf("uri" to "/pharmacology/1", "name" to "Antimicrobial"),
            mapOf("uri" to "/pharmacology/2", "name" to "Antiviral")
        )
        
        val viewData = PharmacologySearchViewData(
            query = "anti",
            pharmacologyEntries = entries
        )
        
        assertEquals("anti", viewData.query)
        assertEquals(2, viewData.pharmacologyEntries.size)
        assertEquals(entries, viewData.pharmacologyEntries)
    }
    
    @Test
    fun testPharmacologySearchViewDataEquality() {
        val entries = listOf(
            mapOf("uri" to "/pharmacology/1", "name" to "Test")
        )
        
        val viewData1 = PharmacologySearchViewData(
            query = "test",
            pharmacologyEntries = entries
        )
        
        val viewData2 = PharmacologySearchViewData(
            query = "test",
            pharmacologyEntries = entries
        )
        
        val viewData3 = PharmacologySearchViewData(
            query = "different",
            pharmacologyEntries = entries
        )
        
        assertEquals(viewData1, viewData2)
        assertNotEquals(viewData1, viewData3)
    }
    
    @Test
    fun testPharmacologySearchViewDataCopy() {
        val originalEntries = listOf(
            mapOf("uri" to "/pharmacology/1", "name" to "Original")
        )
        
        val viewData = PharmacologySearchViewData(
            query = "original",
            pharmacologyEntries = originalEntries
        )
        
        val newEntries = listOf(
            mapOf("uri" to "/pharmacology/2", "name" to "New")
        )
        
        val copy = viewData.copy(
            query = "modified",
            pharmacologyEntries = newEntries
        )
        
        assertEquals("original", viewData.query)
        assertEquals(originalEntries, viewData.pharmacologyEntries)
        assertEquals("modified", copy.query)
        assertEquals(newEntries, copy.pharmacologyEntries)
    }
    
    @Test
    fun testPharmacologySearchViewDataToString() {
        val entries = listOf(
            mapOf("uri" to "/pharmacology/1", "name" to "Test Entry")
        )
        
        val viewData = PharmacologySearchViewData(
            query = "test",
            pharmacologyEntries = entries
        )
        
        val string = viewData.toString()
        assertTrue(string.contains("PharmacologySearchViewData"))
        assertTrue(string.contains("query=test"))
        assertTrue(string.contains("pharmacologyEntries="))
    }
    
    @Test
    fun testPharmacologySearchViewDataHashCode() {
        val entries = listOf(
            mapOf("uri" to "/pharmacology/1", "name" to "Same")
        )
        
        val viewData1 = PharmacologySearchViewData(
            query = "same",
            pharmacologyEntries = entries
        )
        
        val viewData2 = PharmacologySearchViewData(
            query = "same",
            pharmacologyEntries = entries
        )
        
        assertEquals(viewData1.hashCode(), viewData2.hashCode())
    }
    
    @Test
    fun testPharmacologySearchViewDataWithNullQuery() {
        val entries = listOf(
            mapOf("uri" to "/pharmacology/1", "name" to "Entry")
        )
        
        val viewData = PharmacologySearchViewData(
            query = null,
            pharmacologyEntries = entries
        )
        
        assertNull(viewData.query)
        assertEquals(1, viewData.pharmacologyEntries.size)
    }
    
    @Test
    fun testPharmacologySearchViewDataWithEmptyEntries() {
        val viewData = PharmacologySearchViewData(
            query = "search",
            pharmacologyEntries = emptyList()
        )
        
        assertEquals("search", viewData.query)
        assertTrue(viewData.pharmacologyEntries.isEmpty())
    }
    
    @Test
    fun testPharmacologySearchViewDataWithComplexEntries() {
        val entries = listOf(
            mapOf(
                "uri" to "/pharmacology/complex",
                "name" to "Complex Pharmacology",
                "description" to "Additional field that might be present",
                "count" to "42"
            )
        )
        
        val viewData = PharmacologySearchViewData(
            query = "complex",
            pharmacologyEntries = entries
        )
        
        assertEquals("complex", viewData.query)
        assertEquals(1, viewData.pharmacologyEntries.size)
        
        val entry = viewData.pharmacologyEntries[0]
        assertEquals("Complex Pharmacology", entry["name"])
        assertEquals("Additional field that might be present", entry["description"])
        assertEquals("42", entry["count"])
    }
}