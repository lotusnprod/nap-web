package net.nprod.nap.pages.extract

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

class ExtractControllerTest {

    @Test
    fun testCreateDataWithValidExtract() = runBlocking {
        withInMemoryFuseki { server ->
            // Set the SPARQL_SERVER system property
            System.setProperty("SPARQL_SERVER", server.getSparqlEndpoint())
            
            // Add test extract with pharmacy data
            val testData = """
                @prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .
                @prefix n: <https://nap.nprod.net/> .
                
                <https://nap.nprod.net/extract/321> rdf:type n:extract ;
                    n:name "Ethanol Extract" ;
                    n:code "EE321" ;
                    n:description "70% ethanol extract" ;
                    n:number 321 .
                
                # Add pharmacy data
                <https://nap.nprod.net/pharmacy/1> rdf:type n:pharmacy ;
                    n:number 1 ;
                    n:has_extract <https://nap.nprod.net/extract/321> ;
                    n:has_worktype <https://nap.nprod.net/worktype/1> .
                
                <https://nap.nprod.net/pharmacy/2> rdf:type n:pharmacy ;
                    n:number 2 ;
                    n:has_extract <https://nap.nprod.net/extract/321> ;
                    n:has_worktype <https://nap.nprod.net/worktype/2> .
                
                <https://nap.nprod.net/worktype/1> rdf:type n:worktype ;
                    n:name "In Vivo" .
                    
                <https://nap.nprod.net/worktype/2> rdf:type n:worktype ;
                    n:name "In Vitro" .
            """
            
            server.loadN3Data(testData)
            
            val controller = ExtractController()
            val sparqlConnector = SparqlConnector()
            val data = controller.createData("321", sparqlConnector, "https://nap.nprod.net/extract/321")
            
            assertNotNull(data)
            assertEquals("321", data.identifier)
            assertNotNull(data.extract)
            assertEquals("https://nap.nprod.net/extract/321", data.extract.uri)
            assertEquals("Ethanol Extract", data.extract.name)
            assertEquals("EE321", data.extract.code)
            // Check that pharmacy results are returned
            assertNotNull(data.pharmacyResults)
        }
    }

    @Test
    fun testCreateDataWithNonExistentExtract() = runBlocking {
        withInMemoryFuseki { server ->
            // Set the SPARQL_SERVER system property
            System.setProperty("SPARQL_SERVER", server.getSparqlEndpoint())
            
            val controller = ExtractController()
            val sparqlConnector = SparqlConnector()
            
            // Should return null when extract not found in cache
            val data = controller.createData("999", sparqlConnector, "https://nap.nprod.net/extract/999")
            assertNull(data)
        }
    }


    @Test
    fun testGetView() {
        val controller = ExtractController()
        val viewFunction = controller.getView()
        
        // Test that the view function is ExtractView::render
        val testData = ExtractViewData(
            identifier = "test",
            extract = net.nprod.nap.types.Extract("test-uri", "TEST001", "Test Extract"),
            pharmacyResults = emptyList()
        )
        
        // Verify it returns a string (HTML content)
        val result = viewFunction(testData)
        assertTrue(result.isNotEmpty())
        assertTrue(result.contains("html"))
    }

    @Test
    fun testGetEntityType() {
        val controller = ExtractController()
        assertEquals("extract", controller.getEntityType())
    }

    @Test
    fun testRegisterRoutes() = testApplication {
        application {
            System.setProperty("SPARQL_SERVER", "http://localhost:3030/napra/sparql")
            
            routing {
                // Simply verify that registerRoutes doesn't throw an exception
                ExtractController.registerRoutes(this)
            }
        }
        
        // If we get here, route registration succeeded
        assertTrue(true)
    }
}