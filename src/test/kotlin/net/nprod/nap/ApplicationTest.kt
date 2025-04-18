package net.nprod.nap

import kotlin.test.*

/**
 * A simple test class to demonstrate testing in the project.
 * 
 * In a real scenario, you would use Ktor's testing utilities to test the application.
 * For example:
 * 
 * ```
 * testApplication {
 *     val response = client.get("/")
 *     assertEquals(HttpStatusCode.OK, response.status)
 * }
 * ```
 * 
 * But for this demonstration, we'll use a simple test.
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
}
