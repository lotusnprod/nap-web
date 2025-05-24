package net.nprod.nap.test

import org.apache.jena.query.QueryFactory
import org.apache.jena.query.QueryExecutionFactory
import org.apache.jena.system.Txn
import kotlin.test.Test
import kotlin.test.assertTrue
import kotlin.test.assertFalse
import kotlin.test.assertEquals

/**
 * Test the InMemoryFusekiServer functionality independently
 */
class InMemoryFusekiServerTest {
    
    @Test
    fun testServerStartsAndStops() {
        val server = InMemoryFusekiServer()
        
        // Server should not be running initially
        assertFalse(server.isRunning(), "Server should not be running initially")
        
        // Start server
        val endpoint = server.start()
        assertTrue(server.isRunning(), "Server should be running after start")
        assertTrue(endpoint.contains("localhost"), "Endpoint should be on localhost")
        assertTrue(endpoint.contains("napra/sparql"), "Endpoint should have correct path")
        
        // Stop server
        server.stop()
        assertFalse(server.isRunning(), "Server should not be running after stop")
    }
    
    @Test
    fun testDataLoading() {
        val server = InMemoryFusekiServer()
        try {
            server.start()
            
            val dataset = server.getDataset()
            assertNotNull(dataset, "Dataset should not be null")
            
            // Check that test data was loaded
            Txn.executeRead(dataset!!) {
                val model = dataset.defaultModel
                assertTrue(model.size() > 0, "Dataset should contain test data")
            }
            
        } finally {
            server.stop()
        }
    }
    
    @Test
    fun testSparqlQueries() {
        val server = InMemoryFusekiServer()
        try {
            server.start()
            
            val dataset = server.getDataset()
            assertNotNull(dataset, "Dataset should not be null")
            
            // Test basic SPARQL query
            val queryString = """
                PREFIX n: <https://nap.nprod.net/>
                SELECT ?compound ?name WHERE {
                    ?compound a n:compound .
                    ?compound n:name ?name .
                }
            """
            
            Txn.executeRead(dataset!!) {
                val query = QueryFactory.create(queryString)
                QueryExecutionFactory.create(query, dataset).use { qexec ->
                    val results = qexec.execSelect()
                    
                    val compounds = mutableListOf<String>()
                    while (results.hasNext()) {
                        val solution = results.nextSolution()
                        val name = solution.getLiteral("name").string
                        compounds.add(name)
                    }
                    
                    assertTrue(compounds.size > 0, "Should find compounds in test data")
                    assertTrue(compounds.contains("Aspirin"), "Should find Aspirin")
                    assertTrue(compounds.contains("Caffeine"), "Should find Caffeine")
                    assertTrue(compounds.contains("Morphine"), "Should find Morphine")
                }
            }
            
        } finally {
            server.stop()
        }
    }
    
    @Test
    fun testAdditionalDataLoading() {
        val server = InMemoryFusekiServer()
        try {
            server.start()
            
            // Add additional test data
            val additionalData = """
                @prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .
                @prefix n: <https://nap.nprod.net/> .
                
                <https://nap.nprod.net/compound/999> rdf:type n:compound ;
                    n:name "Test Compound" ;
                    n:number "999" .
            """
            
            server.loadN3Data(additionalData)
            
            val dataset = server.getDataset()
            assertNotNull(dataset, "Dataset should not be null")
            
            // Query for the new compound
            val queryString = """
                PREFIX n: <https://nap.nprod.net/>
                SELECT ?name WHERE {
                    <https://nap.nprod.net/compound/999> n:name ?name .
                }
            """
            
            Txn.executeRead(dataset!!) {
                val query = QueryFactory.create(queryString)
                QueryExecutionFactory.create(query, dataset).use { qexec ->
                    val results = qexec.execSelect()
                    
                    assertTrue(results.hasNext(), "Should find the new test compound")
                    val solution = results.nextSolution()
                    val name = solution.getLiteral("name").string
                    assertEquals("Test Compound", name, "Should have correct name")
                }
            }
            
        } finally {
            server.stop()
        }
    }
    
    private fun assertNotNull(value: Any?, message: String) {
        assertTrue(value != null, message)
    }
}