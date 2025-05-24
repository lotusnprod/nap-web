package net.nprod.nap.test

import io.ktor.server.application.*
import io.ktor.server.testing.*
import net.nprod.nap.plugins.configureRouting
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.plugins.contentnegotiation.*
import kotlinx.serialization.json.Json

/**
 * Test application configuration that mirrors the main application
 * but uses test-specific settings and dependencies.
 */
fun Application.testModule(sparqlEndpoint: String) {
    // Configure content negotiation for JSON responses
    install(ContentNegotiation) {
        json(Json {
            prettyPrint = true
            isLenient = true
        })
    }
    
    // Set test environment variables
    System.setProperty("SPARQL_SERVER", sparqlEndpoint)
    System.setProperty("HTTP_AUTH_SPARQL_USER", "test_user")
    System.setProperty("HTTP_AUTH_SPARQL_PASSWORD", "test_password")
    
    // Configure routing
    configureRouting()
}

/**
 * Helper function to create a test application with the given SPARQL endpoint
 */
fun withTestApplication(
    sparqlEndpoint: String,
    test: suspend ApplicationTestBuilder.() -> Unit
) = testApplication {
    application {
        testModule(sparqlEndpoint)
    }
    test()
}

/**
 * Helper function to create a test application with in-memory Fuseki server
 */
fun withInMemoryFuseki(
    testDataResource: String = "/test_data.n3",
    test: suspend ApplicationTestBuilder.(InMemoryFusekiServer) -> Unit
) = testApplication {
    val fusekiServer = InMemoryFusekiServer.createAndStart(testDataResource)
    try {
        application {
            testModule(fusekiServer.getSparqlEndpoint())
        }
        test(fusekiServer)
    } finally {
        fusekiServer.stop()
    }
}