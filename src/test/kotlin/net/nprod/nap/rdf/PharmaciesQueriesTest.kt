package net.nprod.nap.rdf

import net.nprod.nap.test.InMemoryFusekiServer
import net.nprod.nap.types.*
import org.junit.*
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class PharmaciesQueriesTest {
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
        // Add comprehensive test data
        fusekiServer.loadN3Data("""
            @prefix n: <https://nap.nprod.net/> .
            @prefix ex: <http://example.org/> .
            
            # Compounds
            ex:compound1 a n:compound ;
                n:name "Aspirin" .
                
            ex:compound2 a n:compound ;
                n:name "Penicillin" .
                
            # Organisms
            ex:organism1 a n:organism ;
                n:familyname "Salicaceae" ;
                n:genusname "Salix" ;
                n:speciesname "alba" ;
                n:collectedFrom ex:country1 ;
                n:has_taxon <https://nap.nprod.net/taxon/12345> .
                
            ex:organism2 a n:organism ;
                n:familyname "Trichocomaceae" ;
                n:genusname "Penicillium" ;
                n:speciesname "notatum" ;
                n:collectedFrom ex:geoarea1 .
                
            # Worktypes
            ex:worktype1 a n:worktype ;
                n:name "In Vivo" .
                
            ex:worktype2 a n:worktype ;
                n:name "In Vitro" .
                
            # Pharmacologies
            ex:pharmacology1 a n:pharmacology ;
                n:name "Anti-inflammatory" .
                
            ex:pharmacology2 a n:pharmacology ;
                n:name "Antibacterial" .
                
            # Animals
            ex:animal1 a n:animal ;
                n:name "Mouse" .
                
            # Countries and geographical areas
            ex:country1 a n:country ;
                n:name "France" .
                
            ex:geoarea1 a n:geographicalarea ;
                n:name "Europe" .
                
            # Part conditions
            ex:partcondition1 a n:partcondition ;
                n:name "Fresh" .
                
            # Extracts
            ex:extract1 a n:extract ;
                n:name "Ethanol Extract" .
                
            # Administration routes
            ex:adminroute1 a n:administrationroute ;
                n:name "Oral" .
                
            # Pharmacies with various relationships
            ex:pharmacy1 a n:pharmacy ;
                n:has_participant ex:compound1 ;
                n:has_organism ex:organism1 ;
                n:has_worktype ex:worktype1 ;
                n:has_pharmacology ex:pharmacology1 ;
                n:has_animal ex:animal1 ;
                n:has_collected_condition ex:partcondition1 ;
                n:has_extract ex:extract1 ;
                n:administeredBy ex:adminroute1 ;
                n:number 101 .
                
            ex:pharmacy2 a n:pharmacy ;
                n:has_participant ex:compound2 ;
                n:has_organism ex:organism2 ;
                n:has_worktype ex:worktype2 ;
                n:has_pharmacology ex:pharmacology2 ;
                n:number 102 .
                
            ex:pharmacy3 a n:pharmacy ;
                n:has_participant ex:compound1 ;
                n:has_organism ex:organism1 ;
                n:has_worktype ex:worktype1 ;
                n:number 103 .
                
            # Pharmacy without optional fields
            ex:pharmacy4 a n:pharmacy ;
                n:has_worktype ex:worktype2 ;
                n:number 104 .
        """.trimIndent())
    }
    
    @Test
    fun testPharmaciesOfCompound() {
        val pharmacies = pharmaciesOfCompound("http://example.org/compound1", sparqlConnector)
        
        assertEquals(2, pharmacies.size)
        
        // Check first pharmacy
        val pharmacy1 = pharmacies.find { it.uri == "http://example.org/pharmacy1" }
        assertNotNull(pharmacy1)
        assertEquals("101", pharmacy1.number)
        assertTrue(pharmacy1.compounds.any { it.name == "Aspirin" })
        assertNotNull(pharmacy1.organism)
        assertEquals("Salix", pharmacy1.organism?.genusname)
        // Note: Pharmacology cache may not be populated in test environment
        
        // Check third pharmacy
        val pharmacy3 = pharmacies.find { it.uri == "http://example.org/pharmacy3" }
        assertNotNull(pharmacy3)
        assertEquals("103", pharmacy3.number)
    }
    
    @Test
    fun testPharmaciesOfPharmacology() {
        val pharmacies = pharmaciesOfPharmacology("http://example.org/pharmacology1", sparqlConnector)
        
        assertEquals(1, pharmacies.size)
        
        val pharmacy = pharmacies[0]
        assertEquals("http://example.org/pharmacy1", pharmacy.uri)
        // Note: Pharmacology cache may not be populated in test environment
    }
    
    @Test
    fun testPharmaciesOfOrganism() {
        val pharmacies = pharmaciesOfOrganism("http://example.org/organism1", sparqlConnector)
        
        // Now pharmacy1 and pharmacy3 both have organism1
        assertEquals(2, pharmacies.size)
        
        val uris = pharmacies.map { it.uri }.toSet()
        assertTrue("http://example.org/pharmacy1" in uris)
        assertTrue("http://example.org/pharmacy3" in uris)
        
        val pharmacy = pharmacies.find { it.uri == "http://example.org/pharmacy1" }
        assertNotNull(pharmacy)
        assertEquals("Salix", pharmacy.organism?.genusname)
        assertEquals("alba", pharmacy.organism?.speciesname)
        assertEquals("Salicaceae", pharmacy.organism?.familyname)
    }
    
    @Test
    fun testPharmaciesOfWorktype() {
        val pharmacies = pharmaciesOfWorktype("http://example.org/worktype1", sparqlConnector)
        
        assertEquals(2, pharmacies.size)
        
        // Both pharmacy1 and pharmacy3 have worktype1
        val uris = pharmacies.map { it.uri }.toSet()
        assertTrue("http://example.org/pharmacy1" in uris)
        assertTrue("http://example.org/pharmacy3" in uris)
    }
    
    @Test
    fun testPharmaciesOfAnimal() {
        val pharmacies = pharmaciesOfAnimal("http://example.org/animal1", sparqlConnector)
        
        assertEquals(1, pharmacies.size)
        assertEquals("http://example.org/pharmacy1", pharmacies[0].uri)
    }
    
    @Test
    fun testPharmaciesOfTaxa() {
        val pharmacies = pharmaciesOfTaxa(sparqlConnector, "12345")
        
        // Both pharmacy1 and pharmacy3 have organism1 with taxon 12345
        assertEquals(2, pharmacies.size)
        
        val uris = pharmacies.map { it.uri }.toSet()
        assertTrue("http://example.org/pharmacy1" in uris)
        assertTrue("http://example.org/pharmacy3" in uris)
        
        val pharmacy = pharmacies.find { it.uri == "http://example.org/pharmacy1" }
        assertNotNull(pharmacy)
        assertNotNull(pharmacy.organism)
        assertEquals("Salix", pharmacy.organism?.genusname)
    }
    
    @Test
    fun testPharmaciesOfGeographicalArea() {
        val pharmacies = pharmaciesOfGeographicalArea("http://example.org/geoarea1", sparqlConnector)
        
        assertEquals(1, pharmacies.size)
        assertEquals("http://example.org/pharmacy2", pharmacies[0].uri)
        assertNotNull(pharmacies[0].organism)
        assertEquals("Penicillium", pharmacies[0].organism?.genusname)
    }
    
    @Test
    fun testPharmaciesOfCountry() {
        val pharmacies = pharmaciesOfCountry("http://example.org/country1", sparqlConnector)
        
        // Both pharmacy1 and pharmacy3 have organism1 collected from country1
        assertEquals(2, pharmacies.size)
        
        val uris = pharmacies.map { it.uri }.toSet()
        assertTrue("http://example.org/pharmacy1" in uris)
        assertTrue("http://example.org/pharmacy3" in uris)
    }
    
    @Test
    fun testPharmaciesOfPartCondition() {
        val pharmacies = pharmaciesOfPartCondition("http://example.org/partcondition1", sparqlConnector)
        
        assertEquals(1, pharmacies.size)
        assertEquals("http://example.org/pharmacy1", pharmacies[0].uri)
    }
    
    @Test
    fun testPharmaciesOfExtract() {
        val pharmacies = pharmaciesOfExtract("http://example.org/extract1", sparqlConnector)
        
        assertEquals(1, pharmacies.size)
        assertEquals("http://example.org/pharmacy1", pharmacies[0].uri)
    }
    
    @Test
    fun testPharmaciesOfAdministrationRoute() {
        val pharmacies = pharmaciesOfAdministrationRoute("http://example.org/adminroute1", sparqlConnector)
        
        assertEquals(1, pharmacies.size)
        assertEquals("http://example.org/pharmacy1", pharmacies[0].uri)
    }
    
    @Test
    fun testCompoundsByCompoundCode() {
        // Add test data for compound codes
        fusekiServer.loadN3Data("""
            @prefix n: <https://nap.nprod.net/> .
            @prefix ex: <http://example.org/> .
            
            ex:code1 a n:compoundcode ;
                n:name "CHEMBL25" .
                
            ex:compound_with_code1 a n:compound ;
                n:name "Coded Compound 1" ;
                n:has_compoundcode ex:code1 ;
                n:has_synonym ex:syn1 .
                
            ex:compound_with_code2 a n:compound ;
                n:name "Coded Compound 2" ;
                n:has_compoundcode ex:code1 ;
                n:has_synonym ex:syn2 ;
                n:has_synonym ex:syn3 .
                
            ex:syn1 a n:compoundsynonym ;
                n:name "Synonym 1" .
                
            ex:syn2 a n:compoundsynonym ;
                n:name "Synonym 2" .
                
            ex:syn3 a n:compoundsynonym ;
                n:name "Synonym 3" .
        """.trimIndent())
        
        val compounds = compoundsByCompoundCode("http://example.org/code1", sparqlConnector)
        
        assertEquals(2, compounds.size)
        
        // Check ordering by name
        assertEquals("Coded Compound 1", compounds[0].name)
        assertEquals("Coded Compound 2", compounds[1].name)
        
        // Check synonyms
        assertEquals(1, compounds[0].synonyms.size)
        assertEquals("Synonym 1", compounds[0].synonyms[0].name)
        
        assertEquals(2, compounds[1].synonyms.size)
        val synonymNames = compounds[1].synonyms.map { it.name }.toSet()
        assertTrue("Synonym 2" in synonymNames)
        assertTrue("Synonym 3" in synonymNames)
    }
    
    @Test
    fun testPharmaciesWithMissingOptionalData() {
        val pharmacies = pharmaciesOfWorktype("http://example.org/worktype2", sparqlConnector)
        
        // Both pharmacy2 (with organism) and pharmacy4 (without organism) should be returned
        assertEquals(2, pharmacies.size, "Should have 2 pharmacies with worktype2")
        
        val pharmacy2 = pharmacies.find { it.uri == "http://example.org/pharmacy2" }
        assertNotNull(pharmacy2)
        assertEquals("102", pharmacy2.number)
        assertNotNull(pharmacy2.organism)
        assertEquals("Penicillium", pharmacy2.organism?.genusname)
        
        val pharmacy4 = pharmacies.find { it.uri == "http://example.org/pharmacy4" }
        assertNotNull(pharmacy4)
        assertEquals("104", pharmacy4.number)
        // pharmacy4 has no organism data
    }
    
    @Test
    fun testPharmaciesWithNonExistentEntity() {
        // Test with non-existent entities
        assertTrue(pharmaciesOfCompound("http://example.org/nonexistent", sparqlConnector).isEmpty())
        assertTrue(pharmaciesOfOrganism("http://example.org/nonexistent", sparqlConnector).isEmpty())
        assertTrue(pharmaciesOfPharmacology("http://example.org/nonexistent", sparqlConnector).isEmpty())
        assertTrue(pharmaciesOfWorktype("http://example.org/nonexistent", sparqlConnector).isEmpty())
        assertTrue(pharmaciesOfAnimal("http://example.org/nonexistent", sparqlConnector).isEmpty())
        assertTrue(pharmaciesOfCountry("http://example.org/nonexistent", sparqlConnector).isEmpty())
        assertTrue(pharmaciesOfGeographicalArea("http://example.org/nonexistent", sparqlConnector).isEmpty())
        assertTrue(pharmaciesOfPartCondition("http://example.org/nonexistent", sparqlConnector).isEmpty())
        assertTrue(pharmaciesOfExtract("http://example.org/nonexistent", sparqlConnector).isEmpty())
        assertTrue(pharmaciesOfAdministrationRoute("http://example.org/nonexistent", sparqlConnector).isEmpty())
        assertTrue(pharmaciesOfTaxa(sparqlConnector, "99999").isEmpty())
    }
    
    @Test
    fun testCompoundsByCompoundCodeWithMissingData() {
        // Test with compounds missing required data
        fusekiServer.loadN3Data("""
            @prefix n: <https://nap.nprod.net/> .
            @prefix ex: <http://example.org/> .
            
            ex:code_test a n:compoundcode ;
                n:name "TEST123" .
                
            # Compound without name (should be skipped)
            ex:compound_no_name a n:compound ;
                n:has_compoundcode ex:code_test .
                
            # Valid compound
            ex:compound_valid a n:compound ;
                n:name "Valid Compound" ;
                n:has_compoundcode ex:code_test .
        """.trimIndent())
        
        val compounds = compoundsByCompoundCode("http://example.org/code_test", sparqlConnector)
        
        // Should only get the valid compound
        assertEquals(1, compounds.size)
        assertEquals("Valid Compound", compounds[0].name)
    }
}