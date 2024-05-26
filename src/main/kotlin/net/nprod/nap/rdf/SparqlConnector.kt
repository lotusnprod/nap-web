package net.nprod.nap.rdf

import org.apache.jena.query.*
import org.apache.jena.rdf.model.RDFNode
import org.apache.jena.rdf.model.Resource
import org.apache.jena.rdfconnection.RDFConnection
import org.apache.jena.system.Txn


class SparqlConnector {
    val ENDPOINT = "http://nap-sparql:3030/raw/sparql"

    fun getResultsOfQuery(query: String): ResultSet? {
        var safeCopy: ResultSet? = null
        RDFConnection.connect(ENDPOINT).use { conn ->
            Txn.executeRead(conn) {
                val rs = conn.query(query).execSelect()
                safeCopy = ResultSetFactory.copyResults(rs)
            }
        }

        return safeCopy
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
    private fun predicateAndNodeToMap(
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
