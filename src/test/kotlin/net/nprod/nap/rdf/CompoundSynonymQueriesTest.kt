package net.nprod.nap.rdf

import net.nprod.nap.test.InMemoryFusekiServer
import net.nprod.nap.types.Compound
import net.nprod.nap.types.CompoundCode
import net.nprod.nap.types.CompoundSynonym
import org.junit.*
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class CompoundSynonymQueriesTest {
    companion object {
        private lateinit var fusekiServer: InMemoryFusekiServer
        private lateinit var sparqlConnector: SparqlConnector
        
        @JvmStatic
        @BeforeClass
        fun setupServer() {
            fusekiServer = InMemoryFusekiServer()
            val endpoint = fusekiServer.start()
            System.setProperty("SPARQL_SERVER", endpoint)
            sparqlConnector = SparqlConnector()
        }
        
        @JvmStatic
        @AfterClass
        fun teardownServer() {
            fusekiServer.stop()
            System.clearProperty("SPARQL_SERVER")
        }
    }
    
    @Before
    fun setupTestData() {
        // Add test data with compound synonyms
        fusekiServer.loadN3Data("""
            @prefix n: <https://nap.nprod.net/> .
            @prefix ex: <http://example.org/> .
            
            # Synonym 1 - used by two compounds
            ex:synonym1 a n:compoundsynonym ;
                n:name "Aspirin" .
                
            # Synonym 2 - used by one compound  
            ex:synonym2 a n:compoundsynonym ;
                n:name "Acetylsalicylic acid" .
                
            # Synonym 3 - orphan synonym (not used)
            ex:synonym3 a n:compoundsynonym ;
                n:name "Orphan Synonym" .
            
            # Compound 1 with both synonyms
            ex:compound1 a n:compound ;
                n:name "Compound One" ;
                n:synonym ex:synonym1 ;
                n:synonym ex:synonym2 ;
                n:compoundclass "Organic acid" ;
                n:has_compoundcode ex:code1 .
                
            # Compound 2 with one synonym
            ex:compound2 a n:compound ;
                n:name "Compound Two" ;
                n:synonym ex:synonym1 ;
                n:has_compoundcode ex:code2 .
                
            # Compound 3 with no synonyms
            ex:compound3 a n:compound ;
                n:name "Compound Three" ;
                n:compoundclass "Alkaloid" .
                
            # Compound codes
            ex:code1 a n:compoundcode ;
                n:name "CHEMBL25" .
                
            ex:code2 a n:compoundcode ;
                n:name "CID12345" .
        """.trimIndent())
    }
    
    @Test
    fun testCompoundsOfSynonymWithMultipleCompounds() {
        // Test synonym used by multiple compounds
        val compounds = compoundsOfSynonym("http://example.org/synonym1", sparqlConnector)
        
        assertEquals(2, compounds.size)
        
        // Check compounds are ordered by name
        assertEquals("Compound One", compounds[0].name)
        assertEquals("Compound Two", compounds[1].name)
        
        // Check first compound details
        val compound1 = compounds[0]
        assertEquals("http://example.org/compound1", compound1.uri)
        assertEquals("Organic acid", compound1.compoundClass)
        assertNotNull(compound1.compoundCode)
        assertEquals("http://example.org/code1", compound1.compoundCode?.uri)
        assertEquals("CHEMBL25", compound1.compoundCode?.name)
        
        // Check second compound details
        val compound2 = compounds[1]
        assertEquals("http://example.org/compound2", compound2.uri)
        assertNull(compound2.compoundClass)
        assertNotNull(compound2.compoundCode)
        assertEquals("http://example.org/code2", compound2.compoundCode?.uri)
        assertEquals("CID12345", compound2.compoundCode?.name)
    }
    
    @Test
    fun testCompoundsOfSynonymWithSingleCompound() {
        // Test synonym used by single compound
        val compounds = compoundsOfSynonym("http://example.org/synonym2", sparqlConnector)
        
        assertEquals(1, compounds.size)
        
        val compound = compounds[0]
        assertEquals("http://example.org/compound1", compound.uri)
        assertEquals("Compound One", compound.name)
        assertEquals("Organic acid", compound.compoundClass)
    }
    
    @Test
    fun testCompoundsOfSynonymWithNoCompounds() {
        // Test orphan synonym
        val compounds = compoundsOfSynonym("http://example.org/synonym3", sparqlConnector)
        
        assertTrue(compounds.isEmpty())
    }
    
    @Test
    fun testCompoundsOfSynonymWithNonExistentSynonym() {
        // Test non-existent synonym
        val compounds = compoundsOfSynonym("http://example.org/nonexistent", sparqlConnector)
        
        assertTrue(compounds.isEmpty())
    }
    
    @Test
    fun testGetSynonymDetailsWithValidSynonym() {
        val synonym = getSynonymDetails("http://example.org/synonym1", sparqlConnector)
        
        assertNotNull(synonym)
        assertEquals("http://example.org/synonym1", synonym.uri)
        assertEquals("Aspirin", synonym.name)
    }
    
    @Test
    fun testGetSynonymDetailsWithDifferentSynonym() {
        val synonym = getSynonymDetails("http://example.org/synonym2", sparqlConnector)
        
        assertNotNull(synonym)
        assertEquals("http://example.org/synonym2", synonym.uri)
        assertEquals("Acetylsalicylic acid", synonym.name)
    }
    
    @Test
    fun testGetSynonymDetailsWithNonExistentSynonym() {
        val synonym = getSynonymDetails("http://example.org/nonexistent", sparqlConnector)
        
        assertNull(synonym)
    }
    
    @Test
    fun testGetSynonymDetailsWithMissingName() {
        // Add a synonym without a name
        fusekiServer.loadN3Data("""
            @prefix n: <https://nap.nprod.net/> .
            @prefix ex: <http://example.org/> .
            
            ex:synonym_no_name a n:compoundsynonym .
        """.trimIndent())
        
        val synonym = getSynonymDetails("http://example.org/synonym_no_name", sparqlConnector)
        
        assertNull(synonym)
    }
    
    @Test
    fun testCompoundsOfSynonymWithCompoundMissingData() {
        // Add a compound with missing required data
        fusekiServer.loadN3Data("""
            @prefix n: <https://nap.nprod.net/> .
            @prefix ex: <http://example.org/> .
            
            ex:synonym_incomplete a n:compoundsynonym ;
                n:name "Incomplete" .
                
            # Compound without name (should be skipped)
            ex:compound_no_name a n:compound ;
                n:synonym ex:synonym_incomplete .
                
            # Compound without URI (edge case - should be skipped)
            ex:compound_valid a n:compound ;
                n:name "Valid Compound" ;
                n:synonym ex:synonym_incomplete .
        """.trimIndent())
        
        val compounds = compoundsOfSynonym("http://example.org/synonym_incomplete", sparqlConnector)
        
        // Should only get the valid compound
        assertEquals(1, compounds.size)
        assertEquals("Valid Compound", compounds[0].name)
    }
    
    @Test
    fun testCompoundsOfSynonymOrdering() {
        // Add compounds that should be sorted alphabetically
        fusekiServer.loadN3Data("""
            @prefix n: <https://nap.nprod.net/> .
            @prefix ex: <http://example.org/> .
            
            ex:synonym_ordered a n:compoundsynonym ;
                n:name "Common Synonym" .
                
            ex:compound_z a n:compound ;
                n:name "Zebra Compound" ;
                n:synonym ex:synonym_ordered .
                
            ex:compound_a a n:compound ;
                n:name "Apple Compound" ;
                n:synonym ex:synonym_ordered .
                
            ex:compound_m a n:compound ;
                n:name "Middle Compound" ;
                n:synonym ex:synonym_ordered .
        """.trimIndent())
        
        val compounds = compoundsOfSynonym("http://example.org/synonym_ordered", sparqlConnector)
        
        assertEquals(3, compounds.size)
        assertEquals("Apple Compound", compounds[0].name)
        assertEquals("Middle Compound", compounds[1].name)
        assertEquals("Zebra Compound", compounds[2].name)
    }
    
    @Test
    fun testCompoundsOfSynonymWithPartialCompoundCode() {
        // Test compound with code resource but missing name
        fusekiServer.loadN3Data("""
            @prefix n: <https://nap.nprod.net/> .
            @prefix ex: <http://example.org/> .
            
            ex:synonym_partial a n:compoundsynonym ;
                n:name "Partial" .
                
            ex:compound_partial a n:compound ;
                n:name "Partial Compound" ;
                n:synonym ex:synonym_partial ;
                n:has_compoundcode ex:code_no_name .
                
            ex:code_no_name a n:compoundcode .
        """.trimIndent())
        
        val compounds = compoundsOfSynonym("http://example.org/synonym_partial", sparqlConnector)
        
        assertEquals(1, compounds.size)
        // Compound code should not be set since it's missing the name
        assertNull(compounds[0].compoundCode)
    }
}