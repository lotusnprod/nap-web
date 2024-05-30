import kotlinx.html.*
import net.nprod.nap.pages.invalidEntryPage
import net.nprod.nap.pages.presentOutNodes
import net.nprod.nap.rdf.SparqlConnector
import net.nprod.nap.types.Organism
import net.nprod.nap.types.Pharmacology
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
    val nameUri = genNode("name").uri
    val name = outNodes.filter { it.key.uri == nameUri }.values.firstOrNull()?.toString() ?: "Unknown compound"

    // Somehow we can't have a naive CONSTRUCT with no block when we have optionals. That's annoying
    val query = """
            PREFIX n: <https://nap.nprod.net/>
            CONSTRUCT {
                ?pharmacy a n:pharmacy;
                          n:has_participant <$uri>;
                          n:has_worktype ?worktype;
                          n:has_organism ?organism;
                          n:has_pharmacology ?pharmacology.
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
                          
                          OPTIONAL { ?pharmacy n:has_organism ?organism. }
                          OPTIONAL { ?pharmacy n:has_pharmacology ?pharmacology. }
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
            graph.find(triple.subject, genNode("has_worktype"), null).forEach { worktypeTriple ->
                val worktypeUri = worktypeTriple.`object`.uri
                Worktype.Cache[worktypeUri]?.let { pharmacy.worktypes.add(it) }
            }
            graph.find(triple.subject, genNode("has_pharmacology"), null).forEach { pharmacologyTriple ->
                val pharmacologyUri = pharmacologyTriple.`object`.uri
                Pharmacology.Cache[pharmacologyUri]?.let { pharmacy.pharmacologies.add(it) }
            }
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
                pharmacy.organisms.add(organism)
            }
            pharmacyResults.add(pharmacy)
        }
        pharmacyResultsDataset.end()
    }

    return dataPage("$name ($identifier)") {
        id = "content-node"
        h1 { +"$name ($identifier)" }
        h2 { +"Details" }

        div {
           presentOutNodes(outNodes)
        }


        h2 { +"Works" }

        div(classes = "container mt-5") {
            id = "pharmacy"
            table(classes = "table table-striped table-bordered") {
                thead(classes = "thead-dark") {
                    tr {
                        td { +"Experiment" }
                        td { +"Worktypes" }
                        td { +"Pharmacology" }
                        td { +"Organisms" }
                    }
                }
                tbody {
                    pharmacyResults.forEach { pharmacy ->
                        tr {
                            td { a(href = pharmacy.uri) { +pharmacy.uri.getRef() } }
                            td {
                                ul {
                                    pharmacy.worktypes.forEach { worktype ->
                                        li {
                                            classes = setOf("worktype")
                                            a(href = worktype.uri) { +"${worktype.code} - ${worktype.name}" }
                                        }
                                    }
                                }
                            }
                            td {
                                ul {
                                    pharmacy.pharmacologies.forEach { pharmacology ->
                                        li {
                                            classes = setOf("pharmacology")
                                            a(href = pharmacology.uri) { +"${pharmacology.code} - ${pharmacology.name}" }
                                        }
                                    }
                                }
                            }
                            td {
                                if (pharmacy.organisms.size > 0) {
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
            }
        }
    }
}
