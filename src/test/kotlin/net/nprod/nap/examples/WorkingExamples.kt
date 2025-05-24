package net.nprod.nap.examples

import net.nprod.nap.test.withInMemoryFuseki
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Working examples of tests with in-memory Fuseki server and real SPARQL data.
 * 
 * These tests demonstrate that the in-memory Fuseki server is working correctly
 * and the application can successfully load and display data from the test dataset.
 */
class WorkingExamples {
    
    @Test
    fun testCompoundPageWorksWithRealData() = runBlocking {
        withInMemoryFuseki { fusekiServer ->
            println("✅ Fuseki server started at: ${fusekiServer.getSparqlEndpoint()}")
            
            val response = client.get("/compound/1")
            assertEquals(HttpStatusCode.OK, response.status, 
                        "Compound page should load successfully with test data")
            
            val content = response.bodyAsText()
            assertTrue(content.contains("Aspirin", ignoreCase = true), 
                      "Page should show Aspirin from test data")
            
            println("✅ Compound page loaded successfully with test data!")
        }
    }
    
    @Test
    fun testOrganismPageWorksWithRealData() = runBlocking {
        withInMemoryFuseki { fusekiServer ->
            val response = client.get("/organism/1")
            assertEquals(HttpStatusCode.OK, response.status,
                        "Organism page should load successfully with test data")
            
            val content = response.bodyAsText()
            assertTrue(content.contains("Salix alba", ignoreCase = true),
                      "Page should show Salix alba from test data")
            
            println("✅ Organism page loaded successfully with test data!")
        }
    }
    
    @Test
    fun testJsonApiWorksWithRealData() = runBlocking {
        withInMemoryFuseki { fusekiServer ->
            val response = client.get("/compound/1") {
                header(HttpHeaders.Accept, ContentType.Application.Json.toString())
            }
            assertEquals(HttpStatusCode.OK, response.status,
                        "JSON API should work with test data")
            assertEquals(ContentType.Application.Json, response.contentType()?.withoutParameters(),
                        "Should return JSON content type")
            
            val jsonContent = response.bodyAsText()
            assertTrue(jsonContent.contains("Aspirin"),
                      "JSON response should contain test data")
            
            println("✅ JSON API working successfully with test data!")
        }
    }
    
    @Test
    fun testMultipleCompoundsLoad() = runBlocking {
        withInMemoryFuseki { fusekiServer ->
            val compounds = listOf(
                1 to "Aspirin",
                2 to "Caffeine", 
                3 to "Morphine"
            )
            
            for ((id, name) in compounds) {
                val response = client.get("/compound/$id")
                assertEquals(HttpStatusCode.OK, response.status,
                           "Compound $id should load successfully")
                
                val content = response.bodyAsText()
                assertTrue(content.contains(name, ignoreCase = true),
                          "Page should contain $name")
            }
            
            println("✅ All test compounds loaded successfully!")
        }
    }
    
    @Test
    fun testMultipleOrganismsLoad() = runBlocking {
        withInMemoryFuseki { fusekiServer ->
            val organisms = listOf(
                1 to "Salix alba",
                2 to "Coffea arabica",
                3 to "Papaver somniferum"
            )
            
            for ((id, name) in organisms) {
                val response = client.get("/organism/$id")
                assertEquals(HttpStatusCode.OK, response.status,
                           "Organism $id should load successfully")
                
                val content = response.bodyAsText()
                assertTrue(content.contains(name, ignoreCase = true) ||
                          content.contains(name.split(" ")[0], ignoreCase = true),
                          "Page should contain $name or genus")
            }
            
            println("✅ All test organisms loaded successfully!")
        }
    }
}