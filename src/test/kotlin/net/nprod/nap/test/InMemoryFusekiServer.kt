package net.nprod.nap.test

import org.apache.jena.fuseki.main.FusekiServer
import org.apache.jena.query.Dataset
import org.apache.jena.query.DatasetFactory
import org.apache.jena.rdf.model.ModelFactory
import org.apache.jena.riot.RDFDataMgr
import org.apache.jena.riot.Lang
import org.apache.jena.system.Txn
import java.io.InputStream
import java.net.ServerSocket

/**
 * In-memory Fuseki server for testing that loads test data from N3 files.
 * 
 * This creates a real SPARQL endpoint that can be used by the application
 * for comprehensive integration testing.
 */
class InMemoryFusekiServer {
    private var fusekiServer: FusekiServer? = null
    private var dataset: Dataset? = null
    private var serverPort: Int = 0
    private var serverUrl: String = ""
    
    /**
     * Start the in-memory Fuseki server with test data
     */
    fun start(testDataResource: String = "/test_data.n3"): String {
        // Find an available port
        serverPort = findAvailablePort(3330)
        serverUrl = "http://localhost:$serverPort"
        
        // Create in-memory transactional dataset
        dataset = DatasetFactory.createTxnMem()
        
        // Load test data
        loadTestData(testDataResource)
        
        // Create and start Fuseki server
        fusekiServer = FusekiServer.create()
            .port(serverPort)
            .add("/napra", dataset!!)
            .build()
        
        fusekiServer!!.start()
        
        return getSparqlEndpoint()
    }
    
    /**
     * Stop the Fuseki server and clean up resources
     */
    fun stop() {
        fusekiServer?.stop()
        dataset?.close()
        fusekiServer = null
        dataset = null
    }
    
    /**
     * Get the SPARQL query endpoint URL
     */
    fun getSparqlEndpoint(): String = "$serverUrl/napra/sparql"
    
    /**
     * Get the SPARQL update endpoint URL
     */
    fun getSparqlUpdateEndpoint(): String = "$serverUrl/napra/update"
    
    /**
     * Get the dataset for direct access (use within transactions)
     */
    fun getDataset(): Dataset? = dataset
    
    /**
     * Load additional test data from N3 string
     */
    fun loadN3Data(n3Data: String) {
        dataset?.let { ds ->
            Txn.executeWrite(ds) {
                val model = ModelFactory.createDefaultModel()
                RDFDataMgr.read(model, n3Data.byteInputStream(), null, Lang.N3)
                ds.defaultModel.add(model)
            }
        }
    }
    
    /**
     * Load test data from resource file
     */
    private fun loadTestData(resourcePath: String) {
        val inputStream: InputStream = this::class.java.getResourceAsStream(resourcePath)
            ?: throw IllegalArgumentException("Test data resource not found: $resourcePath")
        
        dataset?.let { ds ->
            Txn.executeWrite(ds) {
                val model = ModelFactory.createDefaultModel()
                RDFDataMgr.read(model, inputStream, null, Lang.N3)
                ds.defaultModel.add(model)
            }
        }
    }
    
    /**
     * Find an available port starting from the given port
     */
    private fun findAvailablePort(startPort: Int): Int {
        for (port in startPort until startPort + 100) {
            try {
                ServerSocket(port).use { 
                    return port 
                }
            } catch (e: Exception) {
                // Port is in use, try next one
                continue
            }
        }
        throw RuntimeException("Could not find available port starting from $startPort")
    }
    
    /**
     * Check if the server is running
     */
    fun isRunning(): Boolean = fusekiServer?.let { it.jettyServer?.isRunning } ?: false
    
    companion object {
        /**
         * Create and start a new in-memory Fuseki server instance
         */
        fun createAndStart(testDataResource: String = "/test_data.n3"): InMemoryFusekiServer {
            val server = InMemoryFusekiServer()
            server.start(testDataResource)
            return server
        }
    }
}