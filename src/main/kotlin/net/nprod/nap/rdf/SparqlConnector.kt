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
    }

    fun getResultsOfQuery(query: String): ResultSet? {
        var safeCopy: ResultSet? = null
        RDFConnection.connect(SPARQL_SERVER).use { conn ->
            Txn.executeRead(conn) {
                LOGGER.debug("Query: $query")
                val rs = conn.query(query).execSelect()
                safeCopy = ResultSetFactory.copyResults(rs)
            }
        }

       return safeCopy
    }

    fun constructQueryIntoAQueriableDataset(query: String): Dataset? {
        var datasetGraph: Dataset? = null
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
        return datasetGraph
    }

    fun predicateAndObjectsOf(subject: String): Map<Resource, List<RDFNode>> {
        val query = """
            SELECT ?predicate ?node
            WHERE {
                <$subject> ?predicate ?node
            }
        """.trimIndent()

        return predicateAndNodeToMap(query)
    }

    fun subjectAndPredicatesOf(obj: String): Map<Resource, List<RDFNode>> {
        val query = """
            SELECT ?predicate ?node
            WHERE {
                ?node ?predicate <$obj>
            }
        """.trimIndent()

        return predicateAndNodeToMap(query)
    }

    /**
     * The query should have two results ?predicate ?node
     */
    fun predicateAndNodeToMap(
        query: String
    ): Map<Resource, MutableList<RDFNode>> {
        val output = mutableMapOf<Resource, MutableList<RDFNode>>()
        getResultsOfQuery(query)?.let { rs ->
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
