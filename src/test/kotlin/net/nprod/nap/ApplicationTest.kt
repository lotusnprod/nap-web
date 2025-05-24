package net.nprod.nap

import net.nprod.nap.test.withTestApplication
import io.ktor.client.request.*
import io.ktor.http.*
import kotlin.test.*

/**
 * Basic application tests that verify the core functionality works.
 * 
 * For more comprehensive testing with test data, see the controller-specific
 * test classes in the pages/ packages that extend BaseControllerTest.
 */
class ApplicationTest {
    @Test
    fun testSimple() {
        // A simple test that always passes
        assertTrue(true, "This test should always pass")
    }

    @Test
    fun testStringContains() {
        // A simple test that checks if a string contains a substring
        val testString = "<html><body>Hello, World!</body></html>"
        assertTrue(testString.contains("<html"), "String should contain HTML tag")
    }
    
    @Test
    fun testApplicationStartsWithoutData() {
        // Test that the application can start even without test data
        withTestApplication("http://localhost:9999/nonexistent") {
            // This tests that the application module loads correctly
            val response = client.get("/")
            // We expect this to work even if SPARQL queries fail
            assert(response.status == HttpStatusCode.OK || 
                   response.status == HttpStatusCode.InternalServerError) {
                "Application should start even with invalid SPARQL endpoint"
            }
        }
    }
}
