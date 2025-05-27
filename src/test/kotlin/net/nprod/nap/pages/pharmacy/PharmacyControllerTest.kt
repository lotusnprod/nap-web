package net.nprod.nap.pages.pharmacy

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

class PharmacyControllerTest {

    @Test
    fun testCreateDataWithValidPharmacy() = runBlocking {
        withInMemoryFuseki { server ->
            // Set the SPARQL_SERVER system property
            System.setProperty("SPARQL_SERVER", server.getSparqlEndpoint())
            
            // Add test pharmacy data
            val testData = """
                @prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .
                @prefix n: <https://nap.nprod.net/> .
                
                <https://nap.nprod.net/pharmacy/1> rdf:type n:pharmacy ;
                    n:number 1 ;
                    n:pharmacology <https://nap.nprod.net/pharmacology/1> ;
                    n:organism <https://nap.nprod.net/organism/123> ;
                    n:compound <https://nap.nprod.net/compound/456> ;
                    n:naiveData <https://nap.nprod.net/naivedata/1> ;
                    n:citation <https://nap.nprod.net/citation/1> .
                
                <https://nap.nprod.net/pharmacology/1> rdf:type n:pharmacology ;
                    n:activity "Test Activity" ;
                    n:number 1 .
                
                <https://nap.nprod.net/organism/123> rdf:type n:organism ;
                    n:number 123 ;
                    n:genusname "Test Genus" ;
                    n:speciesname "test species" .
                
                <https://nap.nprod.net/compound/456> rdf:type n:compound ;
                    n:number 456 ;
                    n:SMILES "C=C" .
                
                <https://nap.nprod.net/naivedata/1> rdf:type n:naivedata ;
                    n:qualitativeresult <https://nap.nprod.net/qualitativeresult/1> .
                
                <https://nap.nprod.net/qualitativeresult/1> rdf:type n:qualitativeresult ;
                    n:expression "positive" .
                
                <https://nap.nprod.net/citation/1> rdf:type n:citation ;
                    n:year 2023 ;
                    n:title "Test Citation" .
            """
            
            server.loadN3Data(testData)
            
            val controller = PharmacyController()
            val sparqlConnector = SparqlConnector()
            val data = controller.createData("1", sparqlConnector, "https://nap.nprod.net/pharmacy/1")
            
            assertNotNull(data)
            assertEquals("1", data.identifier)
            assertNotNull(data.pharmacy)
            assertEquals("https://nap.nprod.net/pharmacy/1", data.pharmacy.uri)
        }
    }

    @Test
    fun testCreateDataWithNonExistentPharmacy() = runBlocking {
        withInMemoryFuseki { server ->
            // Set the SPARQL_SERVER system property
            System.setProperty("SPARQL_SERVER", server.getSparqlEndpoint())
            
            val controller = PharmacyController()
            val sparqlConnector = SparqlConnector()
            
            // Should return null when pharmacy not found (exception caught)
            val data = controller.createData("999", sparqlConnector, "https://nap.nprod.net/pharmacy/999")
            assertNull(data)
        }
    }

    @Test
    fun testCreateDataWithInvalidSparqlConnector() = runBlocking {
        // Test with invalid SPARQL endpoint to trigger exception
        System.setProperty("SPARQL_SERVER", "http://invalid-endpoint:9999/sparql")
        
        val controller = PharmacyController()
        val sparqlConnector = SparqlConnector()
        
        // Should catch exception and return null
        val data = controller.createData("1", sparqlConnector, "https://nap.nprod.net/pharmacy/1")
        assertNull(data)
    }

    @Test
    fun testGetView() {
        val controller = PharmacyController()
        val viewFunction = controller.getView()
        
        // Test that the view function is PharmacyView::render
        val testData = PharmacyViewData(
            identifier = "test",
            pharmacy = net.nprod.nap.types.Pharmacy("test-uri")
        )
        
        // Verify it returns a string (HTML content)
        val result = viewFunction(testData)
        assertTrue(result.isNotEmpty())
        assertTrue(result.contains("html"))
    }

    @Test
    fun testGetEntityType() {
        val controller = PharmacyController()
        assertEquals("pharmacy", controller.getEntityType())
    }

    @Test
    fun testRegisterRoutes() = testApplication {
        application {
            System.setProperty("SPARQL_SERVER", "http://localhost:3030/napra/sparql")
            
            routing {
                // Simply verify that registerRoutes doesn't throw an exception
                PharmacyController.registerRoutes(this)
            }
        }
        
        // If we get here, route registration succeeded
        assertTrue(true)
    }
}