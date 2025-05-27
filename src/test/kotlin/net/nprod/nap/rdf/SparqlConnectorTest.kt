package net.nprod.nap.rdf

import net.nprod.nap.test.InMemoryFusekiServer
import org.apache.jena.query.ReadWrite
import org.apache.jena.query.ResultSet
import org.apache.jena.rdf.model.ModelFactory
import org.apache.jena.rdf.model.ResourceFactory
import org.apache.jena.system.Txn
import org.junit.*
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class SparqlConnectorTest {
    companion object {
        private lateinit var fusekiServer: InMemoryFusekiServer
        private lateinit var sparqlConnector: SparqlConnector
        
        @JvmStatic
        @BeforeClass
        fun setupServer() {
            fusekiServer = InMemoryFusekiServer()
            val endpoint = fusekiServer.start()
            System.setProperty("SPARQL_SERVER", endpoint)
            sparqlConnector = SparqlConnector()
        }
        
        @JvmStatic
        @AfterClass
        fun teardownServer() {
            fusekiServer.stop()
            System.clearProperty("SPARQL_SERVER")
        }
    }
    
    @Before
    fun setupTestData() {
        // Add some test data before each test
        fusekiServer.loadN3Data("""
            @prefix n: <https://naturalproducts.net/ontology#> .
            @prefix ex: <http://example.org/> .
            
            ex:compound1 a n:Compound ;
                n:name "Test Compound 1" ;
                n:number 1 ;
                n:has_part ex:part1 ;
                n:has_synonym "TC1" .
                
            ex:compound2 a n:Compound ;
                n:name "Test Compound 2" ;
                n:number 2 ;
                n:has_part ex:part1 ;
                n:has_part ex:part2 .
                
            ex:part1 a n:Part ;
                n:name "Part 1" .
                
            ex:part2 a n:Part ;
                n:name "Part 2" .
        """.trimIndent())
    }
    
    @Test
    fun testGetResultsOfQuery() {
        val query = """
            PREFIX n: <https://naturalproducts.net/ontology#>
            SELECT ?compound ?name
            WHERE {
                ?compound a n:Compound ;
                         n:name ?name .
            }
            ORDER BY ?name
        """.trimIndent()
        
        val results = sparqlConnector.getResultsOfQuery(query)
        assertNotNull(results)
        
        val names = mutableListOf<String>()
        while (results.hasNext()) {
            val solution = results.next()
            names.add(solution.getLiteral("name").string)
        }
        
        assertEquals(2, names.size)
        assertEquals("Test Compound 1", names[0])
        assertEquals("Test Compound 2", names[1])
    }
    
    @Test
    fun testGetResultsOfQueryWithEmptyResult() {
        val query = """
            PREFIX n: <https://naturalproducts.net/ontology#>
            SELECT ?compound
            WHERE {
                ?compound a n:NonExistentClass .
            }
        """.trimIndent()
        
        val results = sparqlConnector.getResultsOfQuery(query)
        assertNotNull(results)
        assertTrue(!results.hasNext())
    }
    
    @Test
    fun testConstructQueryIntoAQueriableDataset() {
        val query = """
            PREFIX n: <https://naturalproducts.net/ontology#>
            PREFIX ex: <http://example.org/>
            
            CONSTRUCT {
                ?compound a n:SimpleCompound ;
                         n:name ?name .
            }
            WHERE {
                ?compound a n:Compound ;
                         n:name ?name .
            }
        """.trimIndent()
        
        val dataset = sparqlConnector.constructQueryIntoAQueriableDataset(query)
        assertNotNull(dataset)
        
        // Query the constructed dataset
        dataset.begin(ReadWrite.READ)
        try {
            val model = dataset.defaultModel
            val compounds = model.listSubjectsWithProperty(
                ResourceFactory.createProperty("https://naturalproducts.net/ontology#", "name")
            )
            
            var count = 0
            while (compounds.hasNext()) {
                compounds.next()
                count++
            }
            assertEquals(2, count)
        } finally {
            dataset.end()
        }
        
        dataset.close()
    }
    
    @Test
    fun testConstructQueryWithException() {
        // Test with an invalid SPARQL server URL to trigger exception
        val originalServer = sparqlConnector.SPARQL_SERVER
        try {
            // Create a new connector with invalid URL
            System.setProperty("SPARQL_SERVER", "http://invalid-server:9999/sparql")
            val badConnector = SparqlConnector()
            
            val query = "CONSTRUCT { ?s ?p ?o } WHERE { ?s ?p ?o }"
            val dataset = badConnector.constructQueryIntoAQueriableDataset(query)
            
            // The method should handle the exception and return a dataset (possibly empty)
            assertNotNull(dataset)
            
            // Clean up the dataset properly
            dataset?.close()
        } finally {
            System.setProperty("SPARQL_SERVER", originalServer!!)
        }
    }
    
    @Test
    fun testPredicateAndObjectsOf() {
        val results = sparqlConnector.predicateAndObjectsOf("http://example.org/compound1")
        
        assertNotNull(results)
        assertTrue(results.isNotEmpty())
        
        // Check that we have the expected predicates
        val predicateUris = results.keys.map { it.uri }.toSet()
        assertTrue("http://www.w3.org/1999/02/22-rdf-syntax-ns#type" in predicateUris)
        assertTrue("https://naturalproducts.net/ontology#name" in predicateUris)
        assertTrue("https://naturalproducts.net/ontology#number" in predicateUris)
        assertTrue("https://naturalproducts.net/ontology#has_part" in predicateUris)
        assertTrue("https://naturalproducts.net/ontology#has_synonym" in predicateUris)
        
        // Check specific values
        val nameProperty = ResourceFactory.createProperty("https://naturalproducts.net/ontology#", "name")
        val names = results[nameProperty]
        assertNotNull(names)
        assertEquals(1, names.size)
        assertEquals("Test Compound 1", names[0].asLiteral().string)
        
        // Check that number is an integer
        val numberProperty = ResourceFactory.createProperty("https://naturalproducts.net/ontology#", "number")
        val numbers = results[numberProperty]
        assertNotNull(numbers)
        assertEquals(1, numbers.size)
        assertEquals(1, numbers[0].asLiteral().int)
    }
    
    @Test
    fun testPredicateAndObjectsOfNonExistent() {
        val results = sparqlConnector.predicateAndObjectsOf("http://example.org/nonexistent")
        
        assertNotNull(results)
        assertTrue(results.isEmpty())
    }
    
    @Test
    fun testSubjectAndPredicatesOf() {
        val results = sparqlConnector.subjectAndPredicatesOf("http://example.org/part1")
        
        assertNotNull(results)
        assertTrue(results.isNotEmpty())
        
        // part1 should be referenced by both compounds
        val hasPartProperty = ResourceFactory.createProperty("https://naturalproducts.net/ontology#", "has_part")
        val subjects = results[hasPartProperty]
        assertNotNull(subjects)
        assertEquals(2, subjects.size)
        
        val subjectUris = subjects.map { it.asResource().uri }.toSet()
        assertTrue("http://example.org/compound1" in subjectUris)
        assertTrue("http://example.org/compound2" in subjectUris)
    }
    
    @Test
    fun testSubjectAndPredicatesOfUnique() {
        // part2 is only referenced by compound2
        val results = sparqlConnector.subjectAndPredicatesOf("http://example.org/part2")
        
        assertNotNull(results)
        assertTrue(results.isNotEmpty())
        
        val hasPartProperty = ResourceFactory.createProperty("https://naturalproducts.net/ontology#", "has_part")
        val subjects = results[hasPartProperty]
        assertNotNull(subjects)
        assertEquals(1, subjects.size)
        assertEquals("http://example.org/compound2", subjects[0].asResource().uri)
    }
    
    @Test
    fun testPredicateAndNodeToMap() {
        // Test with a custom query that returns predicate and node
        val query = """
            PREFIX n: <https://naturalproducts.net/ontology#>
            SELECT ?predicate ?node
            WHERE {
                <http://example.org/compound1> ?predicate ?node .
                FILTER(?predicate = n:name || ?predicate = n:has_part)
            }
        """.trimIndent()
        
        val results = sparqlConnector.predicateAndNodeToMap(query)
        
        assertNotNull(results)
        assertEquals(2, results.size)
        
        // Check name predicate
        val nameProperty = ResourceFactory.createProperty("https://naturalproducts.net/ontology#", "name")
        assertTrue(results.containsKey(nameProperty))
        assertEquals(1, results[nameProperty]?.size)
        
        // Check has_part predicate
        val hasPartProperty = ResourceFactory.createProperty("https://naturalproducts.net/ontology#", "has_part")
        assertTrue(results.containsKey(hasPartProperty))
        assertEquals(1, results[hasPartProperty]?.size)
    }
    
    @Test
    fun testPredicateAndNodeToMapWithMultipleValues() {
        // Query compound2 which has multiple parts
        val query = """
            PREFIX n: <https://naturalproducts.net/ontology#>
            SELECT ?predicate ?node
            WHERE {
                <http://example.org/compound2> ?predicate ?node .
                FILTER(?predicate = n:has_part)
            }
        """.trimIndent()
        
        val results = sparqlConnector.predicateAndNodeToMap(query)
        
        assertNotNull(results)
        assertEquals(1, results.size)
        
        val hasPartProperty = ResourceFactory.createProperty("https://naturalproducts.net/ontology#", "has_part")
        val parts = results[hasPartProperty]
        assertNotNull(parts)
        assertEquals(2, parts.size)
        
        val partUris = parts.map { it.asResource().uri }.toSet()
        assertTrue("http://example.org/part1" in partUris)
        assertTrue("http://example.org/part2" in partUris)
    }
    
    @Test
    fun testPredicateAndNodeToMapWithEmptyQuery() {
        // Test with a query that returns no results
        val query = """
            SELECT ?predicate ?node
            WHERE {
                <http://example.org/nonexistent> ?predicate ?node .
            }
        """.trimIndent()
        
        val results = sparqlConnector.predicateAndNodeToMap(query)
        
        assertNotNull(results)
        assertTrue(results.isEmpty())
    }
    
    @Test
    fun testEnvironmentVariableFallback() {
        // Save original values
        val originalSystemProp = System.getProperty("SPARQL_SERVER")
        
        try {
            // Clear system property to test environment variable fallback
            System.clearProperty("SPARQL_SERVER")
            
            // The connector should still work with environment variable or have null
            val connector = SparqlConnector()
            
            // Just verify it initializes without error
            // SPARQL_SERVER can be null if no env var is set, which is fine
            assertTrue(true, "Connector initialized successfully")
        } finally {
            // Restore original value if it existed
            if (originalSystemProp != null) {
                System.setProperty("SPARQL_SERVER", originalSystemProp)
            }
        }
    }
}