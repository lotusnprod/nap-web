package net.nprod.nap.structure

import net.nprod.nap.test.withInMemoryFuseki
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Tests that verify the application structure and routing work correctly with real SPARQL data.
 * These tests use an in-memory Fuseki server loaded with test data to verify that
 * routes exist and return appropriate responses with actual data.
 */
class ApplicationStructureTest {
    
    @Test
    fun testHomePageRoute() = runBlocking {
        withInMemoryFuseki { fusekiServer ->
            val response = client.get("/")
            // Should load successfully with real SPARQL data
            assertEquals(HttpStatusCode.OK, response.status, "Home route should load successfully")
        }
    }
    
    @Test
    fun testCompoundRoutes() = runBlocking {
        withInMemoryFuseki { fusekiServer ->
            val response = client.get("/compound/1")
            assertEquals(HttpStatusCode.OK, response.status, "Compound route should work with test data")
            
            val content = response.bodyAsText()
            assertTrue(content.contains("Aspirin", ignoreCase = true), 
                      "Compound page should show data from test dataset")
        }
    }
    
    @Test
    fun testOrganismRoutes() = runBlocking {
        withInMemoryFuseki { fusekiServer ->
            val response = client.get("/organism/1")
            assertEquals(HttpStatusCode.OK, response.status, "Organism route should work with test data")
            
            val content = response.bodyAsText()
            assertTrue(content.contains("Salix alba", ignoreCase = true), 
                      "Organism page should show data from test dataset")
        }
    }
    
    @Test
    fun testPharmacyRoutes() = runBlocking {
        withInMemoryFuseki { fusekiServer ->
            val response = client.get("/pharmacy/1")
            assertEquals(HttpStatusCode.OK, response.status, "Pharmacy route should work with test data")
            
            val content = response.bodyAsText()
            // Should contain either compound, organism, or activity data
            val hasData = content.contains("Aspirin", ignoreCase = true) ||
                         content.contains("Salix", ignoreCase = true) ||
                         content.contains("Anti-inflammatory", ignoreCase = true)
            assertTrue(hasData, "Pharmacy page should show related data from test dataset")
        }
    }
    
    @Test
    fun testAdministrationRoutes() = runBlocking {
        withInMemoryFuseki { fusekiServer ->
            val response = client.get("/administrationroute/1")
            assertEquals(HttpStatusCode.OK, response.status, "Administration route should work with test data")
            
            val content = response.bodyAsText()
            assertTrue(content.contains("Oral", ignoreCase = true), 
                      "Administration route page should show data from test dataset")
        }
    }
    
    @Test
    fun testSearchRoutes() = runBlocking {
        withInMemoryFuseki { fusekiServer ->
            // Test compound search
            val compoundSearchResponse = client.get("/compound/search")
            assertEquals(HttpStatusCode.OK, compoundSearchResponse.status, 
                        "Compound search route should work")
            
            // Test organism search
            val organismSearchResponse = client.get("/organism/search")
            assertEquals(HttpStatusCode.OK, organismSearchResponse.status, 
                        "Organism search route should work")
            
            // Test pharmacology search
            val pharmacologySearchResponse = client.get("/pharmacology/search")
            assertEquals(HttpStatusCode.OK, pharmacologySearchResponse.status, 
                        "Pharmacology search route should work")
        }
    }
    
    @Test
    fun testSearchWithQuery() = runBlocking {
        withInMemoryFuseki { fusekiServer ->
            // Test compound search with query
            val response = client.get("/compound/search?query=aspirin")
            assertEquals(HttpStatusCode.OK, response.status, "Search with query should work")
            
            val content = response.bodyAsText()
            assertTrue(content.contains("Aspirin", ignoreCase = true), 
                      "Search should find Aspirin in test data")
        }
    }
    
    @Test
    fun testQueryRoute() = runBlocking {
        withInMemoryFuseki { fusekiServer ->
            val response = client.get("/sparql")
            assertEquals(HttpStatusCode.OK, response.status, "Query route should work")
            
            val content = response.bodyAsText()
            assertTrue(content.contains("sparql", ignoreCase = true) ||
                      content.contains("query", ignoreCase = true),
                      "Query page should contain SPARQL interface")
        }
    }
    
    @Test
    fun testNonExistentRoute() = runBlocking {
        withInMemoryFuseki { fusekiServer ->
            val response = client.get("/nonexistent")
            assertEquals(HttpStatusCode.NotFound, response.status, 
                        "Non-existent routes should return 404")
        }
    }
    
    @Test
    fun testJsonContentNegotiation() = runBlocking {
        withInMemoryFuseki { fusekiServer ->
            val response = client.get("/compound/1") {
                header(HttpHeaders.Accept, ContentType.Application.Json.toString())
            }
            assertEquals(HttpStatusCode.OK, response.status, "JSON API should work with test data")
            assertEquals(ContentType.Application.Json, response.contentType()?.withoutParameters(), 
                        "Should return JSON content type")
            
            val jsonContent = response.bodyAsText()
            assertTrue(jsonContent.contains("Aspirin"), "JSON response should contain test data")
        }
    }
}