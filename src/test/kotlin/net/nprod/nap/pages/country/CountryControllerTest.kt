package net.nprod.nap.pages.country

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

class CountryControllerTest {

    @Test
    fun testCreateDataWithValidCountry() = runBlocking {
        withInMemoryFuseki { server ->
            // Set the SPARQL_SERVER system property
            System.setProperty("SPARQL_SERVER", server.getSparqlEndpoint())
            
            // Add test country with pharmacy data
            val testData = """
                @prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .
                @prefix n: <https://nap.nprod.net/> .
                
                <https://nap.nprod.net/country/99> rdf:type n:country ;
                    n:name "Test Country" ;
                    n:code "TC" ;
                    n:number 99 .
                
                # Add organism collected from this country
                <https://nap.nprod.net/organism/1> rdf:type n:organism ;
                    n:familyname "Test Family" ;
                    n:genusname "Test Genus" ;
                    n:speciesname "test species" ;
                    n:collectedFrom <https://nap.nprod.net/country/99> .
                
                # Add pharmacy data
                <https://nap.nprod.net/pharmacy/1> rdf:type n:pharmacy ;
                    n:number 1 ;
                    n:has_organism <https://nap.nprod.net/organism/1> ;
                    n:has_worktype <https://nap.nprod.net/worktype/1> .
                
                <https://nap.nprod.net/worktype/1> rdf:type n:worktype ;
                    n:name "In Vivo" .
            """
            
            server.loadN3Data(testData)
            
            val controller = CountryController()
            val sparqlConnector = SparqlConnector()
            val data = controller.createData("99", sparqlConnector, "https://nap.nprod.net/country/99")
            
            assertNotNull(data)
            assertEquals("99", data.identifier)
            assertNotNull(data.country)
            assertEquals("https://nap.nprod.net/country/99", data.country.uri)
            assertEquals("Test Country", data.country.name)
            // Check that pharmacy results are returned
            assertNotNull(data.pharmacies)
        }
    }

    @Test
    fun testCreateDataWithNonExistentCountry() = runBlocking {
        withInMemoryFuseki { server ->
            // Set the SPARQL_SERVER system property
            System.setProperty("SPARQL_SERVER", server.getSparqlEndpoint())
            
            val controller = CountryController()
            val sparqlConnector = SparqlConnector()
            
            // Should return null when country not found
            val data = controller.createData("999", sparqlConnector, "https://nap.nprod.net/country/999")
            assertNull(data)
        }
    }


    @Test
    fun testGetView() {
        val controller = CountryController()
        val viewFunction = controller.getView()
        
        // Test that the view function is CountryView::render
        val testData = CountryViewData(
            identifier = "test",
            country = net.nprod.nap.types.Country("test-uri", "Test Country"),
            pharmacies = emptyList()
        )
        
        // Verify it returns a string (HTML content)
        val result = viewFunction(testData)
        assertTrue(result.isNotEmpty())
        assertTrue(result.contains("html"))
    }

    @Test
    fun testGetEntityType() {
        val controller = CountryController()
        assertEquals("country", controller.getEntityType())
    }

    @Test
    fun testRegisterRoutes() = testApplication {
        application {
            System.setProperty("SPARQL_SERVER", "http://localhost:3030/napra/sparql")
            
            routing {
                // Simply verify that registerRoutes doesn't throw an exception
                CountryController.registerRoutes(this)
            }
        }
        
        // If we get here, route registration succeeded
        assertTrue(true)
    }
}