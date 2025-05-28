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
        val trackQueryMethod = SparqlConnector::class.java.getDeclaredMethod("trackQuery", String::class.java, Long::class.java)
        trackQueryMethod.isAccessible = true
        trackQueryMethod.invoke(null, testQuery, 100L)
        
        // Get tracked queries
        val trackedQueries = SparqlConnector.getTrackedQueries()
        
        // Verify
        assertEquals(1, trackedQueries.size)
        assertEquals(testQuery, trackedQueries[0].query)
        assertEquals(100L, trackedQueries[0].executionTimeMs)
    }
    
    @Test
    fun `test query tracking when not enabled`() {
        // Don't start tracking
        
        // Try to track a query
        val testQuery = "SELECT * WHERE { ?s ?p ?o } LIMIT 10"
        
        // Track the query using reflection
        val trackQueryMethod = SparqlConnector::class.java.getDeclaredMethod("trackQuery", String::class.java, Long::class.java)
        trackQueryMethod.isAccessible = true
        trackQueryMethod.invoke(null, testQuery, 50L)
        
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
        val trackQueryMethod = SparqlConnector::class.java.getDeclaredMethod("trackQuery", String::class.java, Long::class.java)
        trackQueryMethod.isAccessible = true
        
        queries.forEachIndexed { index, query ->
            trackQueryMethod.invoke(null, query, (index + 1) * 50L)
        }
        
        // Get tracked queries
        val trackedQueries = SparqlConnector.getTrackedQueries()
        
        // Verify
        assertEquals(3, trackedQueries.size)
        assertEquals(queries[0], trackedQueries[0].query)
        assertEquals(queries[1], trackedQueries[1].query)
        assertEquals(queries[2], trackedQueries[2].query)
        assertEquals(50L, trackedQueries[0].executionTimeMs)
        assertEquals(100L, trackedQueries[1].executionTimeMs)
        assertEquals(150L, trackedQueries[2].executionTimeMs)
    }
    
    @Test
    fun `test clear tracking`() {
        // Start tracking
        SparqlConnector.startTracking()
        
        // Track a query
        val testQuery = "SELECT * WHERE { ?s ?p ?o } LIMIT 10"
        val trackQueryMethod = SparqlConnector::class.java.getDeclaredMethod("trackQuery", String::class.java, Long::class.java)
        trackQueryMethod.isAccessible = true
        trackQueryMethod.invoke(null, testQuery, 75L)
        
        // Verify query was tracked
        assertEquals(1, SparqlConnector.getTrackedQueries().size)
        
        // Clear tracking
        SparqlConnector.clearTracking()
        
        // Verify tracking is cleared
        assertTrue(SparqlConnector.getTrackedQueries().isEmpty())
    }
    
    @Test
    fun `test queries are filtered when logQuery is false`() {
        // Start tracking
        SparqlConnector.startTracking()
        
        // Track various queries with different logQuery settings
        val trackQueryMethod = SparqlConnector::class.java.getDeclaredMethod("trackQuery", String::class.java, Long::class.java, Boolean::class.java)
        trackQueryMethod.isAccessible = true
        
        // Query with logQuery = true (default) - should be tracked
        trackQueryMethod.invoke(null, "SELECT * WHERE { ?s ?p ?o }", 100L, true)
        
        // Queries with logQuery = false - should NOT be tracked
        trackQueryMethod.invoke(null, "SELECT * FROM cache WHERE { ?s ?p ?o }", 50L, false)
        trackQueryMethod.invoke(null, "SELECT ?predicate ?node WHERE { <uri> ?predicate ?node }", 30L, false)
        
        // Another query with logQuery = true - should be tracked
        trackQueryMethod.invoke(null, "CONSTRUCT { ?s ?p ?o } WHERE { ?s ?p ?o }", 200L, true)
        
        // Get tracked queries
        val trackedQueries = SparqlConnector.getTrackedQueries()
        
        // Verify only queries with logQuery = true were tracked
        assertEquals(2, trackedQueries.size)
        assertEquals("SELECT * WHERE { ?s ?p ?o }", trackedQueries[0].query)
        assertEquals("CONSTRUCT { ?s ?p ?o } WHERE { ?s ?p ?o }", trackedQueries[1].query)
    }
}