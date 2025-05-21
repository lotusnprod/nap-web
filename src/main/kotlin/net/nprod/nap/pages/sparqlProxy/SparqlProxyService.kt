package net.nprod.nap.pages.sparqlProxy

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import org.apache.commons.codec.binary.Base64.encodeBase64

/**
 * Service class to handle SPARQL proxy functionality
 * This centralizes the common logic for proxying SPARQL requests
 */
class SparqlProxyService {
    // Environment variables
    private val sparqlServer = System.getenv("SPARQL_SERVER")
    private val httpAuthSparqlUser = System.getenv("HTTP_AUTH_SPARQL_USER") ?: ""
    private val httpAuthSparqlPassword = System.getenv("HTTP_AUTH_SPARQL_PASSWORD") ?: ""
    
    /**
     * Create and configure a new HTTP client
     * @return Configured HttpClient instance
     */
    private fun createHttpClient(): HttpClient {
        return HttpClient(CIO) {
            install(HttpTimeout) {
                requestTimeoutMillis = 60_000
            }
            followRedirects = true
        }
    }
    
    /**
     * Create Basic Authentication header value
     * @return Base64 encoded basic auth header value
     */
    private fun createAuthHeader(): String {
        return "Basic " + encodeBase64("$httpAuthSparqlUser:$httpAuthSparqlPassword".toByteArray()).decodeToString()
    }
    
    /**
     * Handle a POST request to the SPARQL server
     * 
     * @param body The request body to send
     * @return HttpResponse from the SPARQL server
     */
    suspend fun handlePostRequest(body: String): HttpResponse {
        val client = createHttpClient()
        
        return client.post(sparqlServer) {
            headers {
                append(HttpHeaders.Authorization, createAuthHeader())
                append(HttpHeaders.ContentType, "application/x-www-form-urlencoded")
            }
            setBody(body)
        }
    }
    
    /**
     * Handle a GET request to the SPARQL server
     * 
     * @param queryParameters The query parameters to send
     * @return HttpResponse from the SPARQL server
     */
    suspend fun handleGetRequest(queryParameters: Parameters): HttpResponse {
        val client = createHttpClient()
        
        return client.get(sparqlServer) {
            headers {
                append(HttpHeaders.Authorization, createAuthHeader())
            }
            
            url {
                queryParameters.entries().forEach {
                    parameters.append(it.key, it.value.firstOrNull() ?: "")
                }
            }
        }
    }
}