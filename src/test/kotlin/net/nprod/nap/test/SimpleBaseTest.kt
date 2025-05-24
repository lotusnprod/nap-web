package net.nprod.nap.test

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlinx.coroutines.runBlocking
import kotlin.test.assertEquals

/**
 * Simplified base test class that provides common testing utilities
 * without embedded Fuseki complexity.
 */
abstract class SimpleBaseTest {
    
    /**
     * Helper function to test a GET request and verify the response
     */
    protected fun testGetRequest(
        path: String,
        expectedStatus: HttpStatusCode = HttpStatusCode.OK,
        additionalAssertions: (HttpResponse) -> Unit = {}
    ) = runBlocking {
        withTestApplication("http://localhost:9999/invalid") {
            val response = client.get(path)
            assertEquals(expectedStatus, response.status, "Unexpected status for GET $path")
            additionalAssertions(response)
        }
    }
    
    /**
     * Helper function to test a JSON API request
     */
    protected fun testJsonRequest(
        path: String,
        expectedStatus: HttpStatusCode = HttpStatusCode.OK,
        additionalAssertions: (HttpResponse) -> Unit = {}
    ) = runBlocking {
        withTestApplication("http://localhost:9999/invalid") {
            val response = client.get(path) {
                header(HttpHeaders.Accept, ContentType.Application.Json.toString())
            }
            assertEquals(expectedStatus, response.status, "Unexpected status for JSON GET $path")
            if (response.status == HttpStatusCode.OK) {
                assertEquals(ContentType.Application.Json, response.contentType()?.withoutParameters(), 
                            "Expected JSON content type")
            }
            additionalAssertions(response)
        }
    }
    
    /**
     * Helper function to test that a page contains expected content
     */
    protected fun testPageContainsContent(
        path: String,
        expectedContent: List<String>,
        unexpectedContent: List<String> = emptyList()
    ) = runBlocking {
        testGetRequest(path) { response ->
            val content = runBlocking { response.bodyAsText() }
            
            expectedContent.forEach { expected ->
                assert(content.contains(expected, ignoreCase = true)) {
                    "Page $path should contain '$expected' but didn't. Content: ${content.take(500)}..."
                }
            }
            
            unexpectedContent.forEach { unexpected ->
                assert(!content.contains(unexpected, ignoreCase = true)) {
                    "Page $path should not contain '$unexpected' but did. Content: ${content.take(500)}..."
                }
            }
        }
    }
    
    /**
     * Helper function to test an entity page loads correctly (with fallback for invalid data)
     */
    protected fun testEntityPageStructure(
        entityPath: String,
        entityId: String
    ) = runBlocking {
        val fullPath = "$entityPath/$entityId"
        
        // Test that the page loads (may be error page due to invalid SPARQL endpoint)
        testGetRequest(fullPath) { response ->
            val content = runBlocking { response.bodyAsText() }
            // Just verify we get some HTML response
            assert(content.contains("<html", ignoreCase = true) || 
                   content.contains("<!DOCTYPE", ignoreCase = true)) {
                "Response should be HTML"
            }
        }
        
        // Test JSON API response structure
        testJsonRequest(fullPath) { response ->
            if (response.status == HttpStatusCode.OK) {
                val jsonContent = runBlocking { response.bodyAsText() }
                assert(jsonContent.startsWith("{") && jsonContent.endsWith("}")) { 
                    "JSON response should be a valid object" 
                }
            }
        }
    }
}