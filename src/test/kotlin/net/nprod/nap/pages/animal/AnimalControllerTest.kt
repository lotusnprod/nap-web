package net.nprod.nap.pages.animal

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

class AnimalControllerTest {

    @Test
    fun testCreateDataWithValidAnimal() = runBlocking {
        withInMemoryFuseki { server ->
            // Set the SPARQL_SERVER system property
            System.setProperty("SPARQL_SERVER", server.getSparqlEndpoint())
            
            // Add test animal with pharmacy data
            val testData = """
                @prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .
                @prefix n: <https://nap.nprod.net/> .
                
                <https://nap.nprod.net/animal/42> rdf:type n:animal ;
                    n:name "Test Animal" ;
                    n:gender "Male" ;
                    n:code "TA42" ;
                    n:number 42 .
                
                # Add pharmacy data
                <https://nap.nprod.net/pharmacy/1> rdf:type n:pharmacy ;
                    n:number 1 ;
                    n:has_animal <https://nap.nprod.net/animal/42> ;
                    n:has_worktype <https://nap.nprod.net/worktype/1> .
                
                <https://nap.nprod.net/worktype/1> rdf:type n:worktype ;
                    n:name "In Vivo" .
            """
            
            server.loadN3Data(testData)
            
            val controller = AnimalController()
            val sparqlConnector = SparqlConnector()
            val data = controller.createData("42", sparqlConnector, "https://nap.nprod.net/animal/42")
            
            assertNotNull(data)
            assertEquals("42", data.identifier)
            assertNotNull(data.animal)
            assertEquals("https://nap.nprod.net/animal/42", data.animal.uri)
            assertEquals("Test Animal", data.animal.name)
            // Check that pharmacy results are returned
            assertNotNull(data.pharmacyResults)
        }
    }

    @Test
    fun testCreateDataWithNonExistentAnimal() = runBlocking {
        withInMemoryFuseki { server ->
            // Set the SPARQL_SERVER system property
            System.setProperty("SPARQL_SERVER", server.getSparqlEndpoint())
            
            val controller = AnimalController()
            val sparqlConnector = SparqlConnector()
            
            // Should return null when animal not found in cache
            val data = controller.createData("999", sparqlConnector, "https://nap.nprod.net/animal/999")
            assertNull(data)
        }
    }


    @Test
    fun testGetView() {
        val controller = AnimalController()
        val viewFunction = controller.getView()
        
        // Test that the view function is AnimalView::render
        val testData = AnimalViewData(
            identifier = "test",
            animal = net.nprod.nap.types.Animal("test-uri", "Test Animal"),
            pharmacyResults = emptyList()
        )
        
        // Verify it returns a string (HTML content)
        val result = viewFunction(testData)
        assertTrue(result.isNotEmpty())
        assertTrue(result.contains("html"))
    }

    @Test
    fun testGetEntityType() {
        val controller = AnimalController()
        assertEquals("animal", controller.getEntityType())
    }

    @Test
    fun testRegisterRoutes() = testApplication {
        application {
            System.setProperty("SPARQL_SERVER", "http://localhost:3030/napra/sparql")
            
            routing {
                // Simply verify that registerRoutes doesn't throw an exception
                AnimalController.registerRoutes(this)
            }
        }
        
        // If we get here, route registration succeeded
        assertTrue(true)
    }
}