package net.nprod.nap.integration

import net.nprod.nap.test.withInMemoryFuseki
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertTrue
import kotlin.test.assertEquals

/**
 * Integration tests that verify the application works with an in-memory Fuseki server
 * loaded with realistic test data from test_data.n3.
 */
class InMemoryFusekiIntegrationTest {
    
    @Test
    fun testFusekiServerStartsAndLoadsData() = runBlocking {
        withInMemoryFuseki { fusekiServer ->
            // Verify server is running
            assertTrue(fusekiServer.isRunning(), "Fuseki server should be running")
            
            // Verify SPARQL endpoint is accessible
            val endpoint = fusekiServer.getSparqlEndpoint()
            assertTrue(endpoint.contains("localhost"), "Endpoint should be on localhost")
            assertTrue(endpoint.contains("napra/sparql"), "Endpoint should have correct path")
        }
    }
    
    @Test
    fun testCompoundPageWithRealData() = runBlocking {
        withInMemoryFuseki { fusekiServer ->
            val response = client.get("/compound/1")
            
            // Should now work with real data
            assertEquals(HttpStatusCode.OK, response.status, "Compound page should load successfully")
            
            val content = response.bodyAsText()
            assertTrue(content.contains("Aspirin", ignoreCase = true), 
                      "Page should contain compound name from test data")
        }
    }
    
    @Test
    fun testOrganismPageWithRealData() = runBlocking {
        withInMemoryFuseki { fusekiServer ->
            val response = client.get("/organism/1")
            
            assertEquals(HttpStatusCode.OK, response.status, "Organism page should load successfully")
            
            val content = response.bodyAsText()
            assertTrue(content.contains("Salix alba", ignoreCase = true), 
                      "Page should contain organism name from test data")
        }
    }
    
    @Test
    fun testPharmacyPageWithRealData() = runBlocking {
        withInMemoryFuseki { fusekiServer ->
            val response = client.get("/pharmacy/1")
            
            assertEquals(HttpStatusCode.OK, response.status, "Pharmacy page should load successfully")
            
            val content = response.bodyAsText()
            assertTrue(content.contains("Anti-inflammatory", ignoreCase = true) ||
                      content.contains("Aspirin", ignoreCase = true) ||
                      content.contains("Salix alba", ignoreCase = true), 
                      "Page should contain pharmacy data from test data")
        }
    }
    
    @Test
    fun testCompoundSearchWithRealData() = runBlocking {
        withInMemoryFuseki { fusekiServer ->
            val response = client.get("/compound/search?query=aspirin")
            
            assertEquals(HttpStatusCode.OK, response.status, "Compound search should work")
            
            val content = response.bodyAsText()
            assertTrue(content.contains("Aspirin", ignoreCase = true), 
                      "Search should find Aspirin in test data")
        }
    }
    
    @Test
    fun testOrganismSearchWithRealData() = runBlocking {
        withInMemoryFuseki { fusekiServer ->
            val response = client.get("/organism/search?query=salix")
            
            assertEquals(HttpStatusCode.OK, response.status, "Organism search should work")
            
            val content = response.bodyAsText()
            assertTrue(content.contains("Salix", ignoreCase = true), 
                      "Search should find Salix in test data")
        }
    }
    
    @Test
    fun testJsonApiWithRealData() = runBlocking {
        withInMemoryFuseki { fusekiServer ->
            val response = client.get("/compound/1") {
                header(HttpHeaders.Accept, ContentType.Application.Json.toString())
            }
            
            assertEquals(HttpStatusCode.OK, response.status, "JSON API should work")
            assertEquals(ContentType.Application.Json, response.contentType()?.withoutParameters(), 
                        "Should return JSON")
            
            val jsonContent = response.bodyAsText()
            assertTrue(jsonContent.contains("Aspirin"), "JSON should contain compound data")
            assertTrue(jsonContent.startsWith("{") && jsonContent.endsWith("}"), 
                      "Should be valid JSON object")
        }
    }
    
    @Test
    fun testDataRelationshipsWork() = runBlocking {
        withInMemoryFuseki { fusekiServer ->
            // Test that pharmacy entry correctly shows compound-organism relationship
            val response = client.get("/pharmacy/1")
            val content = response.bodyAsText()
            
            // Should show both the compound and organism from the relationship
            val hasAspirin = content.contains("Aspirin", ignoreCase = true)
            val hasSalix = content.contains("Salix", ignoreCase = true) || 
                          content.contains("alba", ignoreCase = true)
            
            assertTrue(hasAspirin || hasSalix, 
                      "Pharmacy page should show data from related compound or organism")
        }
    }
    
    @Test
    fun testMultipleEntitiesLoad() = runBlocking {
        withInMemoryFuseki { fusekiServer ->
            // Test that multiple test entities can be loaded
            val compounds = listOf("/compound/1", "/compound/2", "/compound/3")
            val organisms = listOf("/organism/1", "/organism/2", "/organism/3")
            
            for (path in compounds + organisms) {
                val response = client.get(path)
                assertEquals(HttpStatusCode.OK, response.status, 
                           "Path $path should load successfully with test data")
            }
        }
    }
}