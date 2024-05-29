import kotlinx.html.*
import net.nprod.nap.pages.invalidEntryPage
import net.nprod.nap.pages.presentOutNodes
import net.nprod.nap.rdf.SparqlConnector
import net.nprod.nap.types.Organism
import net.nprod.nap.types.Pharmacy
import net.nprod.nap.types.Worktype
import org.apache.jena.query.ReadWrite
import org.apache.jena.vocabulary.RDF


fun compoundPage(identifier: String?): String {
    if (identifier == null || identifier.toIntOrNull() == null)
        return invalidEntryPage("compound", identifier ?: "null")

    // Generate URI
    val uri = genURI("compound", identifier)

    val sparqlConnector = SparqlConnector()

    // Nodes going out
    val outNodes = sparqlConnector.predicateAndObjectsOf(uri)

    // Somehow we can't have a naive CONSTRUCT with no block when we have optionals. That's annoying
    val query = """
            PREFIX n: <https://nap.nprod.net/>
            CONSTRUCT {
                ?pharmacy a n:pharmacy;
                          n:has_participant <$uri>;
                          n:has_worktype ?worktype;
                          n:has_organism ?organism.
                ?organism n:familyname ?organism_family_name.
                ?organism n:genusname ?organism_genus_name.
                ?organism n:speciesname ?organism_species_name.
                ?organism n:organismclass ?organism_class.
                ?organism_class n:organismclass ?organism_class_name.
           } 
            WHERE {
                ?pharmacy a n:pharmacy;
                          n:has_participant <$uri>;
                          n:has_worktype ?worktype.
                          
                          OPTIONAL {
                             ?pharmacy n:has_organism ?organism.
                          }
                          OPTIONAL { ?organism n:familyname ?organism_family_name. }
                          OPTIONAL { ?organism n:genusname ?organism_genus_name. }
                          OPTIONAL { ?organism n:speciesname ?organism_species_name. }
                          OPTIONAL { ?organism n:organismclass ?organism_class.
                            ?organism_class n:organismclass ?organism_class_name.
                          }
                             
            }
        """.trimIndent()

    val pharmacyResults = mutableListOf<Pharmacy>()

    sparqlConnector.constructQueryIntoAQueriableDataset(query)?.let { pharmacyResultsDataset ->
        pharmacyResultsDataset.begin(ReadWrite.READ)
        val graph = pharmacyResultsDataset.asDatasetGraph().defaultGraph

        val pharmacyResultsIterator = graph.find(null, RDF.type.asNode(), genNode("pharmacy"))
        pharmacyResultsIterator.forEach { triple ->
            val pharmacy = Pharmacy(uri = triple.subject.uri)
            graph.find(triple.subject, genNode("has_worktype"), null).forEach { worktype_triple ->
                val worktypeUri = worktype_triple.`object`.uri
                Worktype.Cache[worktypeUri]?.let {
                    pharmacy.worktypes.add(it)
                }
            }
            graph.find(triple.subject, genNode("has_organism"), null).forEach { organism_triple ->
                val organismUri = organism_triple.`object`.uri
                val organism = Organism(uri = organismUri)
                graph.find(organism_triple.`object`, null, null).forEach { organism_detail_triple ->
                    if (organism_detail_triple.predicate.uri == genNode("familyname").uri) {
                        organism.familyname = organism_detail_triple.`object`.literal.value.toString()
                    }
                    if (organism_detail_triple.predicate.uri == genNode("genusname").uri) {
                        organism.genusname = organism_detail_triple.`object`.literal.value.toString()
                    }
                    if (organism_detail_triple.predicate.uri == genNode("speciesname").uri) {
                        organism.speciesname = organism_detail_triple.`object`.literal.value.toString()
                    }
                }
                pharmacy.organisms.add(organism)
            }
            pharmacyResults.add(pharmacy)
        }
        pharmacyResultsDataset.end()
    }

    return dataPage("Compound page for $identifier") {
        id = "content-node"
        h1 { +"Compound page for $identifier" }
        h2 { +"Out nodes" }
        div { presentOutNodes(outNodes) }

        h2 { +"Works" }

        div {
            id = "pharmacy"
            pharmacyResults.forEach { pharmacy ->
                h3 { a(href=pharmacy.uri) { +"Pharmacy ${pharmacy.uri.getRef()}" } }
                h4 { +"Worktypes" }
                ul {
                    pharmacy.worktypes.forEach { worktype ->
                        li {
                            classes = setOf("worktype")
                            a(href=worktype.uri) { +"${worktype.code} - ${worktype.name}" }
                        }
                    }
                }
                if (pharmacy.organisms.size > 0) {
                    h4 { +"Organisms" }
                    ul {
                        pharmacy.organisms.forEach { organism ->
                            li {
                                classes = setOf("organism")
                                a(href = organism.uri) { +organism.nameForHumans() }
                            }
                        }
                    }
                }
            }
        }
    }
}
