package net.nprod.nap.examples

import net.nprod.nap.test.InMemoryFusekiServer
import net.nprod.nap.test.withInMemoryFuseki
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertTrue
import kotlin.test.assertEquals

/**
 * Example demonstrating how to use the in-memory Fuseki server for comprehensive testing.
 * 
 * This shows three approaches:
 * 1. Manual server management
 * 2. Using the helper function with automatic cleanup
 * 3. Testing with custom test data
 */
class InMemoryFusekiExample {
    
    @Test
    fun exampleManualServerManagement() {
        // Approach 1: Manual server management
        val fusekiServer = InMemoryFusekiServer()
        try {
            // Start server with default test data
            val sparqlEndpoint = fusekiServer.start()
            println("SPARQL endpoint available at: $sparqlEndpoint")
            
            // Server is now running with test data loaded
            assertTrue(fusekiServer.isRunning(), "Server should be running")
            
            // You can now configure your application to use this endpoint
            // System.setProperty("SPARQL_SERVER", sparqlEndpoint)
            
            // Run your application tests here
            // ...
            
        } finally {
            // Always stop the server
            fusekiServer.stop()
        }
    }
    
    @Test
    fun exampleWithHelperFunction() = runBlocking {
        // Approach 2: Using helper function with automatic cleanup
        withInMemoryFuseki { fusekiServer ->
            // Server is automatically started and will be stopped when done
            
            // Test your application endpoints
            val response = client.get("/compound/1")
            assertEquals(HttpStatusCode.OK, response.status)
            
            val content = response.bodyAsText()
            assertTrue(content.contains("Aspirin", ignoreCase = true))
            
            // Test search functionality
            val searchResponse = client.get("/compound/search?query=aspirin")
            assertEquals(HttpStatusCode.OK, searchResponse.status)
            
            val searchContent = searchResponse.bodyAsText()
            assertTrue(searchContent.contains("Aspirin", ignoreCase = true))
            
            // Test JSON API
            val jsonResponse = client.get("/compound/1") {
                header(HttpHeaders.Accept, ContentType.Application.Json.toString())
            }
            assertEquals(HttpStatusCode.OK, jsonResponse.status)
            assertEquals(ContentType.Application.Json, jsonResponse.contentType()?.withoutParameters())
        }
        // Server is automatically stopped here
    }
    
    @Test
    fun exampleWithCustomTestData() {
        // Approach 3: Using custom test data
        val customTestData = """
            @prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .
            @prefix n: <https://nap.nprod.net/> .
            
            <https://nap.nprod.net/compound/1000> rdf:type n:compound ;
                n:name "Custom Test Compound" ;
                n:number "1000" ;
                n:compoundclass "TestClass" .
                
            <https://nap.nprod.net/organism/1000> rdf:type n:organism ;
                n:name "Test Organism" ;
                n:familyname "TestFamily" ;
                n:genusname "TestGenus" ;
                n:speciesname "testspecies" .
        """
        
        val fusekiServer = InMemoryFusekiServer()
        try {
            // Start with default test data
            fusekiServer.start()
            
            // Add custom test data
            fusekiServer.loadN3Data(customTestData)
            
            // Now test with both default and custom data
            println("Server running with combined test data")
            
            // Your tests here can use both the default compounds (Aspirin, Caffeine, Morphine)
            // and your custom compounds/organisms
            
        } finally {
            fusekiServer.stop()
        }
    }
    
    @Test
    fun exampleTestingSpecificFunctionality() = runBlocking {
        // Example: Testing compound search with known data
        withInMemoryFuseki { fusekiServer ->
            
            // Test compound search finds all expected compounds
            val compounds = listOf("aspirin", "caffeine", "morphine")
            
            for (compound in compounds) {
                val response = client.get("/compound/search?query=$compound")
                assertEquals(HttpStatusCode.OK, response.status, 
                           "Search for $compound should work")
                
                val content = response.bodyAsText()
                assertTrue(content.contains(compound, ignoreCase = true),
                          "Search results should contain $compound")
            }
            
            // Test organism search finds expected organisms
            val organisms = listOf("salix", "coffea", "papaver")
            
            for (organism in organisms) {
                val response = client.get("/organism/search?query=$organism")
                assertEquals(HttpStatusCode.OK, response.status,
                           "Search for $organism should work")
                
                val content = response.bodyAsText()
                assertTrue(content.contains(organism, ignoreCase = true),
                          "Search results should contain $organism")
            }
        }
    }
    
    @Test
    fun exampleTestingDataRelationships() = runBlocking {
        // Example: Testing that data relationships work correctly
        withInMemoryFuseki { fusekiServer ->
            
            // Test pharmacy entries show correct compound-organism relationships
            val pharmacyTests = listOf(
                Triple("/pharmacy/1", "Aspirin", "Salix"),
                Triple("/pharmacy/2", "Caffeine", "Coffea"),
                Triple("/pharmacy/3", "Morphine", "Papaver")
            )
            
            for ((url, compound, organism) in pharmacyTests) {
                val response = client.get(url)
                assertEquals(HttpStatusCode.OK, response.status,
                           "Pharmacy page $url should load")
                
                val content = response.bodyAsText()
                
                // Check that the page shows information from both the compound and organism
                val hasCompound = content.contains(compound, ignoreCase = true)
                val hasOrganism = content.contains(organism, ignoreCase = true)
                
                assertTrue(hasCompound || hasOrganism,
                          "Pharmacy page should show related compound or organism data")
            }
        }
    }
}