package net.nprod.nap.pages

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.testing.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.plugins.contentnegotiation.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class BaseControllerTest {
    
    // Test data class
    @Serializable
    data class TestData(
        val id: String,
        val name: String,
        val value: Int
    )
    
    // Test controller implementation
    class TestController : BaseController<TestData> {
        private val testData = mapOf(
            "1" to TestData("1", "Test One", 100),
            "2" to TestData("2", "Test Two", 200),
            "special" to TestData("special", "Special Test", 999)
        )
        
        override fun getData(identifier: String?): TestData? {
            return identifier?.let { testData[it] }
        }
        
        override fun renderHtml(data: TestData): String {
            return """
                <html>
                    <head><title>${data.name}</title></head>
                    <body>
                        <h1>${data.name}</h1>
                        <p>ID: ${data.id}</p>
                        <p>Value: ${data.value}</p>
                    </body>
                </html>
            """.trimIndent()
        }
        
        override fun getEntityType(): String = "TestEntity"
    }
    
    @Test
    fun testGetDataWithValidId() {
        val controller = TestController()
        val data = controller.getData("1")
        
        assertEquals("1", data?.id)
        assertEquals("Test One", data?.name)
        assertEquals(100, data?.value)
    }
    
    @Test
    fun testGetDataWithInvalidId() {
        val controller = TestController()
        val data = controller.getData("999")
        
        assertEquals(null, data)
    }
    
    @Test
    fun testGetDataWithNullId() {
        val controller = TestController()
        val data = controller.getData(null)
        
        assertEquals(null, data)
    }
    
    @Test
    fun testRenderHtml() {
        val controller = TestController()
        val testData = TestData("test", "Test Item", 42)
        val html = controller.renderHtml(testData)
        
        assertTrue(html.contains("<h1>Test Item</h1>"))
        assertTrue(html.contains("ID: test"))
        assertTrue(html.contains("Value: 42"))
        assertTrue(html.contains("<html>"))
        assertTrue(html.contains("</html>"))
    }
    
    @Test
    fun testGetEntityType() {
        val controller = TestController()
        assertEquals("TestEntity", controller.getEntityType())
    }
    
    @Test
    fun testHandleRequestWithValidIdHtml() = testApplication {
        application {
            install(ContentNegotiation) {
                json()
            }
            routing {
                val controller = TestController()
                controller.registerRoute(this, "/test/{id}")
            }
        }
        
        val response = client.get("/test/1")
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(ContentType.Text.Html.withCharset(Charsets.UTF_8), response.contentType())
        
        val body = response.bodyAsText()
        assertTrue(body.contains("<h1>Test One</h1>"))
        assertTrue(body.contains("ID: 1"))
        assertTrue(body.contains("Value: 100"))
    }
    
    @Test
    fun testHandleRequestWithValidIdJson() = testApplication {
        application {
            install(ContentNegotiation) {
                json()
            }
            routing {
                val controller = TestController()
                controller.registerRoute(this, "/test/{id}")
            }
        }
        
        val response = client.get("/test/2") {
            header(HttpHeaders.Accept, "application/json")
        }
        assertEquals(HttpStatusCode.OK, response.status)
        
        val body = response.bodyAsText()
        val data = Json.decodeFromString<TestData>(body)
        assertEquals("2", data.id)
        assertEquals("Test Two", data.name)
        assertEquals(200, data.value)
    }
    
    @Test
    fun testHandleRequestWithInvalidIdHtml() = testApplication {
        application {
            install(ContentNegotiation) {
                json()
            }
            routing {
                val controller = TestController()
                controller.registerRoute(this, "/test/{id}")
            }
        }
        
        val response = client.get("/test/invalid")
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(ContentType.Text.Html.withCharset(Charsets.UTF_8), response.contentType())
        
        val body = response.bodyAsText()
        // Should return the invalid entry page
        assertTrue(body.contains("Invalid"))
        assertTrue(body.contains("TestEntity"))
        assertTrue(body.contains("invalid"))
    }
    
    @Test
    fun testHandleRequestWithInvalidIdJson() = testApplication {
        application {
            install(ContentNegotiation) {
                json()
            }
            routing {
                val controller = TestController()
                controller.registerRoute(this, "/test/{id}")
            }
        }
        
        val response = client.get("/test/invalid") {
            header(HttpHeaders.Accept, "application/json")
        }
        assertEquals(HttpStatusCode.NotFound, response.status)
        
        val body = response.bodyAsText()
        assertTrue(body.contains("error"))
        assertTrue(body.contains("Resource not found"))
    }
    
    @Test
    fun testHandleRequestWithNullId() = testApplication {
        application {
            install(ContentNegotiation) {
                json()
            }
            routing {
                val controller = TestController()
                // Register a route that doesn't require an ID parameter
                get("/test") {
                    controller.handleRequest(call, null)
                }
            }
        }
        
        val response = client.get("/test")
        assertEquals(HttpStatusCode.OK, response.status)
        
        val body = response.bodyAsText()
        assertTrue(body.contains("Invalid"))
        assertTrue(body.contains("TestEntity"))
        assertTrue(body.contains("null"))
    }
    
    @Test
    fun testHandleRequestWithSpecialCharacterId() = testApplication {
        application {
            install(ContentNegotiation) {
                json()
            }
            routing {
                val controller = TestController()
                controller.registerRoute(this, "/test/{id}")
            }
        }
        
        val response = client.get("/test/special")
        assertEquals(HttpStatusCode.OK, response.status)
        
        val body = response.bodyAsText()
        assertTrue(body.contains("<h1>Special Test</h1>"))
        assertTrue(body.contains("Value: 999"))
    }
    
    @Test
    fun testHandleRequestWithMixedAcceptHeader() = testApplication {
        application {
            install(ContentNegotiation) {
                json()
            }
            routing {
                val controller = TestController()
                controller.registerRoute(this, "/test/{id}")
            }
        }
        
        // Test with Accept header that includes JSON among other types
        val response = client.get("/test/1") {
            header(HttpHeaders.Accept, "text/html, application/json, */*")
        }
        assertEquals(HttpStatusCode.OK, response.status)
        
        val body = response.bodyAsText()
        // Should return JSON since it's in the Accept header
        val data = Json.decodeFromString<TestData>(body)
        assertEquals("1", data.id)
    }
    
    @Test
    fun testRegisterRoute() = testApplication {
        application {
            install(ContentNegotiation) {
                json()
            }
            routing {
                val controller = TestController()
                // Test registering multiple routes
                controller.registerRoute(this, "/entity/{id}")
                controller.registerRoute(this, "/api/entity/{id}")
            }
        }
        
        // Test first route
        val response1 = client.get("/entity/1")
        assertEquals(HttpStatusCode.OK, response1.status)
        assertTrue(response1.bodyAsText().contains("Test One"))
        
        // Test second route
        val response2 = client.get("/api/entity/1")
        assertEquals(HttpStatusCode.OK, response2.status)
        assertTrue(response2.bodyAsText().contains("Test One"))
    }
    
    @Test
    fun testHandleRequestDirectCall() = testApplication {
        val controller = TestController()
        
        application {
            install(ContentNegotiation) {
                json()
            }
            routing {
                // Test calling handleRequest directly
                get("/direct/{id}") {
                    controller.handleRequest(call, call.parameters["id"])
                }
            }
        }
        
        val response = client.get("/direct/2")
        assertEquals(HttpStatusCode.OK, response.status)
        assertTrue(response.bodyAsText().contains("Test Two"))
    }
}