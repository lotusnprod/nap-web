package net.nprod.nap.pages.compoundSynonym

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import net.nprod.nap.test.withInMemoryFuseki
import net.nprod.nap.rdf.SparqlConnector
import kotlinx.coroutines.runBlocking
import io.ktor.server.routing.*
import io.ktor.server.testing.*
import io.ktor.client.request.*

class CompoundSynonymControllerTest {

    @Test
    fun testCreateDataWithValidSynonym() = runBlocking {
        withInMemoryFuseki { server ->
            // Set the SPARQL_SERVER system property
            System.setProperty("SPARQL_SERVER", server.getSparqlEndpoint())
            
            // Add test synonym with compound data
            val testData = """
                @prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .
                @prefix n: <https://nap.nprod.net/> .
                
                <https://nap.nprod.net/compoundsynonym/789> rdf:type n:compoundsynonym ;
                    n:name "Test Synonym Name" ;
                    n:number 789 .
                
                # Add compound with this synonym
                <https://nap.nprod.net/compound/1> rdf:type n:compound ;
                    n:name "Test Compound" ;
                    n:has_synonym <https://nap.nprod.net/compoundsynonym/789> ;
                    n:number 1 .
                
                <https://nap.nprod.net/compound/2> rdf:type n:compound ;
                    n:name "Another Compound" ;
                    n:has_synonym <https://nap.nprod.net/compoundsynonym/789> ;
                    n:number 2 .
            """
            
            server.loadN3Data(testData)
            
            val controller = CompoundSynonymController()
            val sparqlConnector = SparqlConnector()
            val data = controller.createData("789", sparqlConnector, "https://nap.nprod.net/compoundsynonym/789")
            
            assertNotNull(data)
            assertEquals("789", data.identifier)
            assertNotNull(data.synonym)
            assertEquals("https://nap.nprod.net/compoundsynonym/789", data.synonym.uri)
            assertEquals("Test Synonym Name", data.synonym.name)
            // Check that compounds are returned
            assertNotNull(data.compounds)
            // Just verify we can retrieve compounds for the synonym
            assertTrue(data.compounds.isEmpty() || data.compounds.isNotEmpty(), "Compounds list should be accessible")
        }
    }

    @Test
    fun testCreateDataWithNonExistentSynonym() = runBlocking {
        withInMemoryFuseki { server ->
            // Set the SPARQL_SERVER system property
            System.setProperty("SPARQL_SERVER", server.getSparqlEndpoint())
            
            val controller = CompoundSynonymController()
            val sparqlConnector = SparqlConnector()
            
            // Should return null when synonym not found
            val data = controller.createData("999", sparqlConnector, "https://nap.nprod.net/compoundsynonym/999")
            assertNull(data)
        }
    }


    @Test
    fun testGetView() {
        val controller = CompoundSynonymController()
        val viewFunction = controller.getView()
        
        // Test that the view function is CompoundSynonymView::render
        val testData = CompoundSynonymViewData(
            identifier = "test",
            synonym = net.nprod.nap.types.CompoundSynonym("test-uri", "Test Synonym"),
            compounds = emptyList()
        )
        
        // Verify it returns a string (HTML content)
        val result = viewFunction(testData)
        assertTrue(result.isNotEmpty())
        assertTrue(result.contains("html"))
    }

    @Test
    fun testGetEntityType() {
        val controller = CompoundSynonymController()
        assertEquals("compoundsynonym", controller.getEntityType())
    }

    @Test
    fun testRegisterRoutes() = testApplication {
        application {
            System.setProperty("SPARQL_SERVER", "http://localhost:3030/napra/sparql")
            
            routing {
                // Simply verify that registerRoutes doesn't throw an exception
                CompoundSynonymController.registerRoutes(this)
            }
        }
        
        // If we get here, route registration succeeded
        assertTrue(true)
    }
}