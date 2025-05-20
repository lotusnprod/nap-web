package net.nprod.nap.rdf

import genNode
import io.ktor.util.logging.*
import net.nprod.nap.types.*
import org.apache.jena.query.Query
import org.apache.jena.query.QueryExecutionFactory
import org.apache.jena.query.QueryFactory
import org.apache.jena.query.QuerySolution
import org.apache.jena.query.ReadWrite
import org.apache.jena.rdf.model.Model
import org.apache.jena.rdf.model.ModelFactory
import org.apache.jena.vocabulary.RDF

internal val LOGGER = KtorSimpleLogger("net.nprod.pharmacyquery")


fun pharmaciesFromQuery(
    sparqlConnector: SparqlConnector,
    query: String
): MutableList<Pharmacy> {
    LOGGER.info("Query started")
    val pharmacyResults = mutableListOf<Pharmacy>()
    sparqlConnector.constructQueryIntoAQueriableDataset(query)?.let { pharmacyResultsDataset ->
        LOGGER.info("Query returns results")
        pharmacyResultsDataset.begin(ReadWrite.READ)
        val graph = pharmacyResultsDataset.asDatasetGraph().defaultGraph
        val model = ModelFactory.createModelForGraph(graph)

        val pharmacyResultsIterator = graph.find(null, RDF.type.asNode(), genNode("pharmacy"))
        pharmacyResultsIterator.forEach { triple ->
            val pharmacy = Pharmacy(uri = triple.subject.uri)
            graph.find(triple.subject, genNode("has_worktype"), null).forEach { worktypeTriple ->
                val worktypeUri = worktypeTriple.`object`.uri
                Worktype.Cache[worktypeUri]?.let { pharmacy.worktypes.add(it) }
            }
            
            // Get number property for sorting
            graph.find(triple.subject, genNode("number"), null).nextOptional().ifPresent {
                pharmacy.number = it.`object`.literal.value.toString()
            }

            val compoundsQuery = """
                PREFIX n: <https://nap.nprod.net/>
                SELECT ?uri ?name WHERE {
                   <${triple.subject.uri}> n:has_participant ?uri. 
                   ?uri a n:compound;
                        n:name ?name.
                }
            """.asQuery()

            compoundsQuery.runOn(model) { result ->
               val compound = Compound(uri = result.getResource("uri").toString())
               compound.name = result.getLiteral("name").string
               pharmacy.compounds.add(compound)
            }

            if (pharmacy.pharmacology == null) {
                graph.find(triple.subject, genNode("has_pharmacology"), null).nextOptional().ifPresent {
                    val pharmacologyUri = it.`object`.uri
                    pharmacy.pharmacology = Pharmacology.Cache[pharmacologyUri]
                }
            }
            if (pharmacy.organism == null) {
                graph.find(triple.subject, genNode("has_organism"), null).forEach { organismTriple ->
                    val organismUri = organismTriple.`object`.uri
                    val organism = Organism(uri = organismUri)
                    graph.find(organismTriple.`object`, null, null).forEach { organismDetailTriple ->
                        if (organismDetailTriple.predicate.uri == genNode("familyname").uri) {
                            organism.familyname = organismDetailTriple.`object`.literal.value.toString()
                        }
                        if (organismDetailTriple.predicate.uri == genNode("genusname").uri) {
                            organism.genusname = organismDetailTriple.`object`.literal.value.toString()
                        }
                        if (organismDetailTriple.predicate.uri == genNode("speciesname").uri) {
                            organism.speciesname = organismDetailTriple.`object`.literal.value.toString()
                        }
                    }
                    pharmacy.organism = organism
                }
                pharmacyResults.add(pharmacy)
            }
        }
        pharmacyResultsDataset.end()
    }
    LOGGER.info("Finished populating the pharmacy list")
    return pharmacyResults
}

private fun Query.runOn(model: Model, any: (QuerySolution) -> Unit) {
    QueryExecutionFactory.create(this, model).use { qexec ->
        val results = qexec.execSelect()

        while (results.hasNext()) {
            any(results.nextSolution())
        }
    }
}

private fun String.asQuery(): Query {
    return QueryFactory.create(this.trimIndent())
}
