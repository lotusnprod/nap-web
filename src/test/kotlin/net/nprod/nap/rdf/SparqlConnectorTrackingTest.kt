package net.nprod.nap.rdf

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.AfterEach
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SparqlConnectorTrackingTest {
    
    @BeforeEach
    fun setup() {
        // Ensure clean state before each test
        SparqlConnector.clearTracking()
    }
    
    @AfterEach
    fun cleanup() {
        // Clean up after each test
        SparqlConnector.clearTracking()
    }
    
    @Test
    fun `test query tracking when enabled`() {
        // Start tracking
        SparqlConnector.startTracking()
        
        // Create a test query
        val testQuery = "SELECT * WHERE { ?s ?p ?o } LIMIT 10"
        
        // Track the query using reflection to access the private method
        val trackQueryMethod = SparqlConnector::class.java.getDeclaredMethod("trackQuery", String::class.java)
        trackQueryMethod.isAccessible = true
        trackQueryMethod.invoke(null, testQuery)
        
        // Get tracked queries
        val trackedQueries = SparqlConnector.getTrackedQueries()
        
        // Verify
        assertEquals(1, trackedQueries.size)
        assertEquals(testQuery, trackedQueries[0])
    }
    
    @Test
    fun `test query tracking when not enabled`() {
        // Don't start tracking
        
        // Try to track a query
        val testQuery = "SELECT * WHERE { ?s ?p ?o } LIMIT 10"
        
        // Track the query using reflection
        val trackQueryMethod = SparqlConnector::class.java.getDeclaredMethod("trackQuery", String::class.java)
        trackQueryMethod.isAccessible = true
        trackQueryMethod.invoke(null, testQuery)
        
        // Get tracked queries
        val trackedQueries = SparqlConnector.getTrackedQueries()
        
        // Verify nothing was tracked
        assertTrue(trackedQueries.isEmpty())
    }
    
    @Test
    fun `test multiple queries tracking`() {
        // Start tracking
        SparqlConnector.startTracking()
        
        // Track multiple queries
        val queries = listOf(
            "SELECT * WHERE { ?s ?p ?o } LIMIT 10",
            "SELECT ?name WHERE { ?s rdfs:label ?name }",
            "CONSTRUCT { ?s ?p ?o } WHERE { ?s ?p ?o } LIMIT 100"
        )
        
        // Track queries using reflection
        val trackQueryMethod = SparqlConnector::class.java.getDeclaredMethod("trackQuery", String::class.java)
        trackQueryMethod.isAccessible = true
        
        queries.forEach { query ->
            trackQueryMethod.invoke(null, query)
        }
        
        // Get tracked queries
        val trackedQueries = SparqlConnector.getTrackedQueries()
        
        // Verify
        assertEquals(3, trackedQueries.size)
        assertEquals(queries, trackedQueries)
    }
    
    @Test
    fun `test clear tracking`() {
        // Start tracking
        SparqlConnector.startTracking()
        
        // Track a query
        val testQuery = "SELECT * WHERE { ?s ?p ?o } LIMIT 10"
        val trackQueryMethod = SparqlConnector::class.java.getDeclaredMethod("trackQuery", String::class.java)
        trackQueryMethod.isAccessible = true
        trackQueryMethod.invoke(null, testQuery)
        
        // Verify query was tracked
        assertEquals(1, SparqlConnector.getTrackedQueries().size)
        
        // Clear tracking
        SparqlConnector.clearTracking()
        
        // Verify tracking is cleared
        assertTrue(SparqlConnector.getTrackedQueries().isEmpty())
    }
}