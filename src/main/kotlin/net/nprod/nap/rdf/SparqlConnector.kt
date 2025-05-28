package net.nprod.nap.rdf

import org.apache.jena.query.*
import org.apache.jena.rdf.model.RDFNode
import org.apache.jena.rdf.model.Resource
import org.apache.jena.rdfconnection.RDFConnection
import org.apache.jena.system.Txn
import org.apache.jena.tdb2.TDB2Factory
import org.slf4j.LoggerFactory


class SparqlConnector {
    // get SPARQL_SERVER from system property first, then environment variable, or set default for production docker compose
    val SPARQL_SERVER = System.getProperty("SPARQL_SERVER") ?: System.getenv("SPARQL_SERVER")
    
    companion object {
        private val LOGGER = LoggerFactory.getLogger(SparqlConnector::class.java)
        
        // Data class to store query info
        data class QueryInfo(
            val query: String,
            val executionTimeMs: Long
        )
        
        // Thread-local storage for tracking queries
        private val queriesThreadLocal = ThreadLocal<MutableList<QueryInfo>>()
        
        /**
         * Start tracking SPARQL queries for the current thread
         */
        fun startTracking() {
            queriesThreadLocal.set(mutableListOf())
        }
        
        /**
         * Get all tracked queries for the current thread
         */
        fun getTrackedQueries(): List<QueryInfo> {
            return queriesThreadLocal.get()?.toList() ?: emptyList()
        }
        
        /**
         * Clear tracked queries for the current thread
         */
        fun clearTracking() {
            queriesThreadLocal.remove()
        }
        
        /**
         * Track a query if tracking is enabled and logging is requested
         */
        private fun trackQuery(query: String, executionTimeMs: Long, logQuery: Boolean = true) {
            if (!logQuery) {
                return
            }
            
            queriesThreadLocal.get()?.add(QueryInfo(query, executionTimeMs))
        }
    }

    fun getResultsOfQuery(query: String, logQuery: Boolean = true): ResultSet? {
        var safeCopy: ResultSet? = null
        val startTime = System.currentTimeMillis()
        
        RDFConnection.connect(SPARQL_SERVER).use { conn ->
            Txn.executeRead(conn) {
                LOGGER.debug("Query: $query")
                val rs = conn.query(query).execSelect()
                safeCopy = ResultSetFactory.copyResults(rs)
            }
        }
        
        val executionTime = System.currentTimeMillis() - startTime
        trackQuery(query, executionTime, logQuery)

       return safeCopy
    }

    fun constructQueryIntoAQueriableDataset(query: String, logQuery: Boolean = true): Dataset? {
        var datasetGraph: Dataset? = null
        val startTime = System.currentTimeMillis()
        val arqQuery = QueryFactory.create(query, Syntax.syntaxARQ)
        
        try {
            val qExec = QueryExecution.service(SPARQL_SERVER).query(arqQuery).build()
            datasetGraph = TDB2Factory.createDataset()

            datasetGraph.begin(ReadWrite.WRITE)
            qExec.execConstructDataset(datasetGraph)
            datasetGraph.commit()
            datasetGraph.end()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        
        val executionTime = System.currentTimeMillis() - startTime
        trackQuery(query, executionTime, logQuery)
        
        return datasetGraph
    }

    fun predicateAndObjectsOf(subject: String): Map<Resource, List<RDFNode>> {
        val query = """
            SELECT ?predicate ?node
            WHERE {
                <$subject> ?predicate ?node
            }
        """.trimIndent()

        return predicateAndNodeToMap(query, logQuery = false)
    }

    fun subjectAndPredicatesOf(obj: String): Map<Resource, List<RDFNode>> {
        val query = """
            SELECT ?predicate ?node
            WHERE {
                ?node ?predicate <$obj>
            }
        """.trimIndent()

        return predicateAndNodeToMap(query, logQuery = false)
    }

    /**
     * The query should have two results ?predicate ?node
     */
    fun predicateAndNodeToMap(
        query: String,
        logQuery: Boolean = true
    ): Map<Resource, MutableList<RDFNode>> {
        val output = mutableMapOf<Resource, MutableList<RDFNode>>()
        getResultsOfQuery(query, logQuery)?.let { rs ->
            while (rs.hasNext()) {
                val qs = rs.next()
                if (output.containsKey(qs.getResource("predicate"))) {
                    output[qs.getResource("predicate")]!!.add(qs.get("node"))
                } else {
                    output[qs.getResource("predicate")] = mutableListOf(qs.get("node"))
                }
            }
        }
        return output
    }
}
