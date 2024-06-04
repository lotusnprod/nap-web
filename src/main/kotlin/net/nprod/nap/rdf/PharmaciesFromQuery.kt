package net.nprod.nap.rdf

import genNode
import net.nprod.nap.types.Organism
import net.nprod.nap.types.Pharmacology
import net.nprod.nap.types.Pharmacy
import net.nprod.nap.types.Worktype
import org.apache.jena.query.ReadWrite
import org.apache.jena.vocabulary.RDF

fun pharmaciesFromQuery(
    sparqlConnector: SparqlConnector,
    query: String
): MutableList<Pharmacy> {
    val pharmacyResults = mutableListOf<Pharmacy>()

    sparqlConnector.constructQueryIntoAQueriableDataset(query)?.let { pharmacyResultsDataset ->
        pharmacyResultsDataset.begin(ReadWrite.READ)
        val graph = pharmacyResultsDataset.asDatasetGraph().defaultGraph

        val pharmacyResultsIterator = graph.find(null, RDF.type.asNode(), genNode("pharmacy"))
        pharmacyResultsIterator.forEach { triple ->
            val pharmacy = Pharmacy(uri = triple.subject.uri)
            graph.find(triple.subject, genNode("has_worktype"), null).forEach { worktypeTriple ->
                val worktypeUri = worktypeTriple.`object`.uri
                Worktype.Cache[worktypeUri]?.let { pharmacy.worktypes.add(it) }
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
    return pharmacyResults
}