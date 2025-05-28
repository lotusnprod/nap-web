package net.nprod.nap.pages

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlinx.coroutines.runBlocking
import net.nprod.nap.module
import net.nprod.nap.rdf.SparqlConnector
import net.nprod.nap.test.InMemoryFusekiServer
import org.junit.jupiter.api.*
import kotlin.test.assertTrue
import kotlin.test.assertEquals

class SparqlQueryTrackingIntegrationTest {
    companion object {
        private lateinit var fusekiServer: InMemoryFusekiServer

        @BeforeAll
        @JvmStatic
        fun setUpClass() {
            fusekiServer = InMemoryFusekiServer()
            fusekiServer.start()
            System.setProperty("SPARQL_SERVER", fusekiServer.getSparqlEndpoint())
        }

        @AfterAll
        @JvmStatic
        fun tearDownClass() {
            fusekiServer.stop()
            System.clearProperty("SPARQL_SERVER")
        }
    }

    @BeforeEach
    fun setUp() {
        // Clear any existing tracking
        SparqlConnector.clearTracking()
    }

    @Test
    fun `test SPARQL queries are displayed on compound search page`() = testApplication {
        application {
            module()
        }

        // Make a request to the compound search page with a query
        val response = client.get("/compound/search?query=test")
        
        assertEquals(HttpStatusCode.OK, response.status)
        val html = response.bodyAsText()
        
        // Check that the SPARQL queries section is present
        assertTrue(html.contains("Show SPARQL Queries"), "HTML should contain SPARQL queries toggle")
        assertTrue(html.contains("sparql-queries"), "HTML should contain the queries div")
        assertTrue(html.contains("copyQuery"), "HTML should contain the copy function")
    }

    @Test
    fun `test SPARQL queries are displayed on entity pages`() = testApplication {
        application {
            module()
        }

        // Add test data for a compound
        fusekiServer.loadN3Data("""
            @prefix n: <http://napradev.org/schema/> .
            @prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> .
            
            <http://napradev.org/compound/1> a n:Compound ;
                n:number 1 ;
                rdfs:label "Test Compound" ;
                n:compoundClass "Test Class" .
        """.trimIndent())

        // Make a request to the compound page
        val response = client.get("/compound/1")
        
        assertEquals(HttpStatusCode.OK, response.status)
        val html = response.bodyAsText()
        
        // Check that the SPARQL queries section is present
        assertTrue(html.contains("Show SPARQL Queries"), "HTML should contain SPARQL queries toggle")
        assertTrue(html.contains("sparql-queries"), "HTML should contain the queries div")
        
        // Check that queries contain expected patterns
        assertTrue(html.contains("SELECT"), "Should contain SELECT queries")
        assertTrue(html.contains("compound/1"), "Should contain queries for the specific compound")
    }

    @Test
    fun `test no SPARQL queries section when no queries are executed`() = testApplication {
        application {
            module()
        }

        // Make a request to the home page (which doesn't execute SPARQL queries)
        val response = client.get("/")
        
        assertEquals(HttpStatusCode.OK, response.status)
        val html = response.bodyAsText()
        
        // Check that the SPARQL queries section is NOT present
        assertTrue(!html.contains("Show SPARQL Queries"), "HTML should not contain SPARQL queries when none are executed")
    }
}