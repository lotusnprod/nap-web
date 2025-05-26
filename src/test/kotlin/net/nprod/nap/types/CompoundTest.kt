package net.nprod.nap.types

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import net.nprod.nap.test.withInMemoryFuseki
import net.nprod.nap.rdf.SparqlConnector
import kotlinx.coroutines.runBlocking

class CompoundTest {

    @Test
    fun testFromSparqlWithFullData() = runBlocking {
        withInMemoryFuseki { server ->
            // Set the SPARQL_SERVER system property
            System.setProperty("SPARQL_SERVER", server.getSparqlEndpoint())
            
            // Add test compound with all fields
            val testData = """
                @prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .
                @prefix n: <https://nap.nprod.net/> .
                
                <https://nap.nprod.net/compound/123> rdf:type n:compound ;
                    n:compoundclass "Alkaloid" ;
                    n:name "Test Compound" ;
                    n:number 123 ;
                    n:publicationCode "TC001" ;
                    n:publicationName "Test Publication" ;
                    n:has_compoundcode <https://nap.nprod.net/compoundcode/1> ;
                    n:synonym <https://nap.nprod.net/synonym/1> ;
                    n:synonym <https://nap.nprod.net/synonym/2> .
                
                <https://nap.nprod.net/compoundcode/1> n:name "SMILES Code" .
                <https://nap.nprod.net/synonym/1> n:name "Synonym One" .
                <https://nap.nprod.net/synonym/2> n:name "Synonym Two" .
            """
            
            server.loadN3Data(testData)
            
            val sparqlConnector = SparqlConnector()
            val compound = Compound.fromSparql(sparqlConnector, "https://nap.nprod.net/compound/123")
            
            // Verify all fields
            assertEquals("https://nap.nprod.net/compound/123", compound.uri)
            assertEquals("Alkaloid", compound.compoundClass)
            assertEquals("Test Compound", compound.name)
            assertEquals(null, compound.number)  // number is not in SELECT clause
            assertEquals("TC001", compound.publicationCode)
            assertEquals("Test Publication", compound.publicationName)
            assertNotNull(compound.compoundCode)
            assertEquals("https://nap.nprod.net/compoundcode/1", compound.compoundCode?.uri)
            assertEquals("SMILES Code", compound.compoundCode?.name)
            assertEquals(2, compound.synonyms.size)
            val synonymNames = compound.synonyms.map { it.name }.sorted()
            assertEquals(listOf("Synonym One", "Synonym Two"), synonymNames)
        }
    }

    @Test
    fun testFromSparqlWithMinimalData() = runBlocking {
        withInMemoryFuseki { server ->
            // Set the SPARQL_SERVER system property
            System.setProperty("SPARQL_SERVER", server.getSparqlEndpoint())
            
            // Add test compound with only required fields
            val testData = """
                @prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .
                @prefix n: <https://nap.nprod.net/> .
                
                <https://nap.nprod.net/compound/456> rdf:type n:compound ;
                    n:compoundclass "Terpenoid" ;
                    n:name "Minimal Compound" ;
                    n:number 456 .
            """
            
            server.loadN3Data(testData)
            
            val sparqlConnector = SparqlConnector()
            val compound = Compound.fromSparql(sparqlConnector, "https://nap.nprod.net/compound/456")
            
            // Verify required fields
            assertEquals("https://nap.nprod.net/compound/456", compound.uri)
            assertEquals("Terpenoid", compound.compoundClass)
            assertEquals("Minimal Compound", compound.name)
            assertEquals(null, compound.number)  // number is not in SELECT clause
            assertEquals(null, compound.publicationCode)
            assertEquals(null, compound.publicationName)
            assertEquals(null, compound.compoundCode)
            assertEquals(0, compound.synonyms.size)
        }
    }

    @Test
    fun testFromSparqlWithNonExistentCompound() = runBlocking {
        withInMemoryFuseki { server ->
            // Set the SPARQL_SERVER system property
            System.setProperty("SPARQL_SERVER", server.getSparqlEndpoint())
            
            val sparqlConnector = SparqlConnector()
            
            // Should throw exception when compound not found
            assertFailsWith<Exception> {
                Compound.fromSparql(sparqlConnector, "https://nap.nprod.net/compound/999")
            }
        }
    }

    @Test
    fun testFromSparqlWithCompoundCodeButNoName() = runBlocking {
        withInMemoryFuseki { server ->
            // Set the SPARQL_SERVER system property
            System.setProperty("SPARQL_SERVER", server.getSparqlEndpoint())
            
            // Add compound with code but missing code name
            // Note: Due to SPARQL query structure, compound code without name won't be returned
            val testData = """
                @prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .
                @prefix n: <https://nap.nprod.net/> .
                
                <https://nap.nprod.net/compound/789> rdf:type n:compound ;
                    n:compoundclass "Phenolic" ;
                    n:name "Code Test Compound" ;
                    n:number 789 ;
                    n:has_compoundcode <https://nap.nprod.net/compoundcode/2> .
                
                # Note: compoundcode exists but has no name property
                <https://nap.nprod.net/compoundcode/2> rdf:type n:compoundcode .
            """
            
            server.loadN3Data(testData)
            
            val sparqlConnector = SparqlConnector()
            val compound = Compound.fromSparql(sparqlConnector, "https://nap.nprod.net/compound/789")
            
            // Check basic fields first
            assertEquals("https://nap.nprod.net/compound/789", compound.uri)
            assertEquals("Code Test Compound", compound.name)
            
            // Since the compound code has no name property, the OPTIONAL block won't match
            // and compound.compoundCode will be null
            assertEquals(null, compound.compoundCode)
        }
    }

    @Test
    fun testFromSparqlWithMultipleSynonyms() = runBlocking {
        withInMemoryFuseki { server ->
            // Set the SPARQL_SERVER system property
            System.setProperty("SPARQL_SERVER", server.getSparqlEndpoint())
            
            // Add compound with multiple synonyms
            val testData = """
                @prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .
                @prefix n: <https://nap.nprod.net/> .
                
                <https://nap.nprod.net/compound/321> rdf:type n:compound ;
                    n:compoundclass "Peptide" ;
                    n:name "Multi-Synonym Compound" ;
                    n:number 321 ;
                    n:synonym <https://nap.nprod.net/synonym/10> ;
                    n:synonym <https://nap.nprod.net/synonym/11> ;
                    n:synonym <https://nap.nprod.net/synonym/12> .
                
                <https://nap.nprod.net/synonym/10> n:name "First Synonym" .
                <https://nap.nprod.net/synonym/11> n:name "Second Synonym" .
                <https://nap.nprod.net/synonym/12> n:name "Third Synonym" .
            """
            
            server.loadN3Data(testData)
            
            val sparqlConnector = SparqlConnector()
            val compound = Compound.fromSparql(sparqlConnector, "https://nap.nprod.net/compound/321")
            
            // Should have all three synonyms (order not guaranteed)
            assertEquals(3, compound.synonyms.size)
            val synonymNames = compound.synonyms.map { it.name }.sorted()
            assertEquals(listOf("First Synonym", "Second Synonym", "Third Synonym"), synonymNames)
        }
    }

    @Test
    fun testFromSparqlWithPartialSynonymData() = runBlocking {
        withInMemoryFuseki { server ->
            // Set the SPARQL_SERVER system property
            System.setProperty("SPARQL_SERVER", server.getSparqlEndpoint())
            
            // Add compound with synonym that has no name
            val testData = """
                @prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .
                @prefix n: <https://nap.nprod.net/> .
                
                <https://nap.nprod.net/compound/555> rdf:type n:compound ;
                    n:compoundclass "Glycoside" ;
                    n:name "Partial Synonym Compound" ;
                    n:number 555 ;
                    n:synonym <https://nap.nprod.net/synonym/20> ;
                    n:synonym <https://nap.nprod.net/synonym/21> .
                
                <https://nap.nprod.net/synonym/20> n:name "Valid Synonym" .
                # Note: synonym/21 exists but has no name
                <https://nap.nprod.net/synonym/21> rdf:type n:synonym .
            """
            
            server.loadN3Data(testData)
            
            val sparqlConnector = SparqlConnector()
            val compound = Compound.fromSparql(sparqlConnector, "https://nap.nprod.net/compound/555")
            
            // Should only have the synonym with a name
            assertEquals(1, compound.synonyms.size)
            assertEquals("Valid Synonym", compound.synonyms[0].name)
        }
    }
}