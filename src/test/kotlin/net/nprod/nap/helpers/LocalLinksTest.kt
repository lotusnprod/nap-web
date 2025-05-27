package net.nprod.nap.helpers

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.BeforeTest
import kotlin.test.AfterTest

class LocalLinksTest {
    
    private var originalEnvironment: String? = null
    
    @BeforeTest
    fun setUp() {
        // Save the original ENVIRONMENT value
        originalEnvironment = System.getenv("ENVIRONMENT")
    }
    
    @AfterTest
    fun tearDown() {
        // Restore the original ENVIRONMENT value
        // Note: We can't actually set environment variables in Java/Kotlin at runtime
        // So we'll test the behavior as-is
    }
    
    @Test
    fun testLocalLinksInProductionMode() {
        // When ENVIRONMENT is not "development" (default case)
        // Assuming ENVIRONMENT is not set to "development" in test environment
        
        val uri1 = "https://nap.nprod.net/compound/123"
        val result1 = localLinks(uri1)
        assertEquals("/compound/123", result1)
        
        val uri2 = "https://nap.nprod.net/organism/456"
        val result2 = localLinks(uri2)
        assertEquals("/organism/456", result2)
        
        val uri3 = "https://nap.nprod.net/pharmacy/789"
        val result3 = localLinks(uri3)
        assertEquals("/pharmacy/789", result3)
    }
    
    @Test
    fun testLocalLinksWithTrailingSlash() {
        val uri = "https://nap.nprod.net/"
        val result = localLinks(uri)
        assertEquals("/", result)
    }
    
    @Test
    fun testLocalLinksWithoutPath() {
        val uri = "https://nap.nprod.net"
        val result = localLinks(uri)
        // In production mode, only URIs with trailing slash are replaced
        assertEquals("https://nap.nprod.net", result)
    }
    
    @Test
    fun testLocalLinksWithDifferentDomain() {
        // Test that URIs from other domains are not modified
        val uri1 = "https://example.com/some/path"
        val result1 = localLinks(uri1)
        assertEquals("https://example.com/some/path", result1)
        
        val uri2 = "http://localhost:8080/test"
        val result2 = localLinks(uri2)
        assertEquals("http://localhost:8080/test", result2)
    }
    
    @Test
    fun testLocalLinksWithQueryParameters() {
        val uri = "https://nap.nprod.net/compound/123?param=value"
        val result = localLinks(uri)
        assertEquals("/compound/123?param=value", result)
    }
    
    @Test
    fun testLocalLinksWithFragment() {
        val uri = "https://nap.nprod.net/compound/123#section"
        val result = localLinks(uri)
        assertEquals("/compound/123#section", result)
    }
    
    @Test
    fun testLocalLinksEmptyString() {
        val uri = ""
        val result = localLinks(uri)
        assertEquals("", result)
    }
    
    @Test
    fun testLocalLinksComplexPath() {
        val uri = "https://nap.nprod.net/admin/users/edit/42"
        val result = localLinks(uri)
        assertEquals("/admin/users/edit/42", result)
    }
    
    // Note: Testing development mode would require mocking System.getenv()
    // which is complex in Kotlin/Java. The current tests cover the production path.
}