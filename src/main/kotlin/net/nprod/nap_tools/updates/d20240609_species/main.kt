package net.nprod.nap_tools.updates.d20240609_species

import org.apache.jena.query.*
import org.apache.jena.sparql.core.Transactional
import org.apache.jena.tdb2.TDB2Factory
import org.apache.jena.update.UpdateExecutionFactory
import org.apache.jena.update.UpdateFactory
import org.apache.jena.vocabulary.RDF

enum class TaxonomicLevel {
    CLASS,
    FAMILY,
    GENUS,
    SPECIES,
    SUBSPECIES
}

data class Organism(
    val className: String,
    val family: String,
    val genus: String,
    val species: String,
    val subspecies: String,
)

data class Taxon(
    val parentTaxon: MutableSet<Taxon> = mutableSetOf(),
    val taxonomicLevel: TaxonomicLevel,
    val name: String,
    var newId: Int = 0,
    val ancestry: String
)

class TaxaSet : MutableSet<Taxon> by mutableSetOf() {
    val taxaMap = mutableMapOf<Triple<TaxonomicLevel, String, String>, Taxon>()

    // This is gross, but as computers are fast who cares?
    fun addOrGet(taxonomicLevel: TaxonomicLevel, name: String, ancestry: String): Taxon {
        val triple = Triple(taxonomicLevel, name, ancestry)
        return taxaMap[triple] ?: Taxon(taxonomicLevel = taxonomicLevel, name = name, ancestry = ancestry).also {
            taxaMap[triple] = it
        }
    }
}


object D20240609species {
    // Gather all the species, genus, families
    // group them
    // create a new :
    //  a n:taxon ; n:name name; n:taxonomic_level .... ; n:parent_taxon
    // associate the taxon to the organism
    @JvmStatic
    fun main(args: Array<String>) {
        val dataset = TDB2Factory.connectDataset("data/tdb_nap_raw")
  
        //
        // DO NOT UNDO ONCE IT IS PUBLIC OR YOU WILL MESS UP THE IDs
        //
        undo(dataset)
        checkRanOnce(dataset)

        val organismMap = generateOrganismMap(dataset)

        // Now that we have the map, we can generate all the new taxa
        // We could have done that cleaner but that will do
        val taxaSet = TaxaSet()
        val taxaToOriginalOrganisms = mutableMapOf<Taxon, MutableSet<String>>()

        organismMap.keys.toList().forEach { organism ->

            var parentalityString = "" // A way to uniquify each level

            val classTaxa = if (organism.className != "") {
                taxaSet.addOrGet(
                    taxonomicLevel = TaxonomicLevel.CLASS,
                    name = organism.className,
                    ancestry = parentalityString).also {
                    taxaSet.add(it)
                }
            } else {
                null
            }

            parentalityString += organism.className.replace("_", "-") + "_"

            val familyTaxa = if (organism.family != "") {
                taxaSet.addOrGet(
                    taxonomicLevel = TaxonomicLevel.FAMILY,
                    name = organism.family,
                    ancestry = parentalityString
                ).also { taxa ->
                    classTaxa?.let { taxa.parentTaxon.add(it) }
                    taxaSet.add(taxa)
                }
            } else {
                null
            }

            parentalityString += organism.family.replace("_", "-") + "_"

            val genusTaxa = if (organism.genus != "") {
                taxaSet.addOrGet(
                    taxonomicLevel = TaxonomicLevel.GENUS,
                    name = organism.genus,
                    ancestry = parentalityString
                ).also { taxa ->
                    familyTaxa?.let { taxa.parentTaxon.add(it) }
                    taxaSet.add(taxa)
                }
            } else {
                null
            }

            parentalityString += organism.genus.replace("_", "-") + "_"

            val speciesTaxa = if (organism.species != "") {
                taxaSet.addOrGet(
                    taxonomicLevel = TaxonomicLevel.SPECIES,
                    name = organism.species,
                    ancestry = parentalityString
                ).also { taxa ->
                    genusTaxa?.let { taxa.parentTaxon.add(it) }
                    taxaSet.add(taxa)
                }
            } else {
                null
            }

            parentalityString += organism.species.replace("_", "-") + "_"

            val subspeciesTaxa = if (organism.subspecies != "") {
                taxaSet.addOrGet(
                    taxonomicLevel = TaxonomicLevel.SUBSPECIES,
                    name = organism.subspecies,
                    ancestry = parentalityString
                ).also { taxa ->
                    speciesTaxa?.let { taxa.parentTaxon.add(it) }
                    taxaSet.add(taxa)
                }
            } else {
                null
            }

            val taxa = listOfNotNull(familyTaxa, genusTaxa, speciesTaxa, subspeciesTaxa)
            if (taxa.isNotEmpty()) {
                taxaToOriginalOrganisms[taxa.last()] = organismMap[organism]!!
            }
        }

        // Now give them all a number
        var id = 0
        taxaSet.forEach {
            it.newId = id++
        }

        println("Writing to the dataset")

        dataset.executeWrite {
            val model = dataset.defaultModel
            val taxonType = model.createResource("https://nap.nprod.net/taxon")
            taxaToOriginalOrganisms.forEach { (taxa, organisms) ->
                val rdfTaxon = model.createResource("https://nap.nprod.net/taxon/${taxa.newId}")
                rdfTaxon.addProperty(RDF.type, taxonType)
                rdfTaxon.addProperty(model.createProperty("https://nap.nprod.net/name"), model.createLiteral(taxa.name))
                rdfTaxon.addProperty(RDF.type, model.createResource("https://nap.nprod.net/taxon"))
                rdfTaxon.addProperty(
                    model.createProperty("https://nap.nprod.net/taxonomic_level"),
                    model.createLiteral(taxa.taxonomicLevel.name)
                )
                taxa.parentTaxon.forEach { parentTaxa ->
                    rdfTaxon.addProperty(
                        model.createProperty("https://nap.nprod.net/parent_taxon"),
                        model.createResource("https://nap.nprod.net/taxon/${parentTaxa.newId}")
                    )
                }
                // Now for each of the organisms, we need to link them to the taxa
                organisms.forEach { organismUri ->
                    model.createResource(organismUri)
                        .addProperty(model.createProperty("https://nap.nprod.net/has_taxon"), rdfTaxon)
                }
            }
            val update = model.createResource("https://nap.nprod.net/update/20240609_species")
            update.addProperty(
                model.createProperty("https://nap.nprod.net/name"),
                model.createLiteral("20240609_species")
            )
            model.add(update, RDF.type, model.createProperty("https://nap.nprod.net/update"))
        }

        dataset.close()
    }

    private fun generateOrganismMap(dataset: Dataset): MutableMap<Organism, MutableSet<String>> {
        println("Generating organism map")
        val organismMap = mutableMapOf<Organism, MutableSet<String>>()
        dataset.executeRead<Transactional> {
            val query = """
                    PREFIX n: <https://nap.nprod.net/>
                    SELECT ?organism ?class ?family ?genus ?species ?subspecies {
                     ?organism a n:organism;
                             n:organismclass/n:name ?class;
                             n:familyname ?family;
                             n:genusname ?genus;
                             n:speciesname ?species;
                             n:subspeciesname ?subspecies.
                    }
                """.trimIndent()
            val arqQuery = QueryFactory.create(query, Syntax.syntaxARQ)
            val qExec = QueryExecutionFactory.create(arqQuery, dataset)
            qExec.execSelect().let {
                while (it.hasNext()) {
                    val row = it.next()
                    val organism = row.getResource("organism")
                    val className = row.getLiteral("class").string
                    val family = row.getLiteral("family").string
                    val genus = row.getLiteral("genus").string
                    val species = row.getLiteral("species").string
                    val subspecies = row.getLiteral("subspecies").string
                    val organismDC = Organism(className, family, genus, species, subspecies)
                    if (organismMap[organismDC] == null) {
                        organismMap[organismDC] = mutableSetOf()
                    }
                    organismMap[organismDC]!!.add(organism.uri) // It has a default
                }
            }
        }
        return organismMap
    }

    private fun checkRanOnce(dataset: Dataset) {
        println("Checking if this update has been run already")
        dataset.executeRead<Transactional> {
            val updateCheckQuery = """
                    PREFIX n: <https://nap.nprod.net/>
                    SELECT ?update {
                        ?update a n:update.
                        ?update n:name "20240609_species".
                    }
                """.trimIndent()

            val arqUpdateCheckQuery = QueryFactory.create(updateCheckQuery, Syntax.syntaxARQ)
            val qExecUpdateCheck = QueryExecutionFactory.create(arqUpdateCheckQuery, dataset)
            if (qExecUpdateCheck.execSelect().hasNext()) {
                println("This update has been run already, and it would make an horrible mess if you run it again as ids may not be repeatable")
                return@executeRead
            }
        }
    }

    private fun undo(dataset: Dataset) {
        println("Undoing")
        dataset.executeWrite {
            // A sparql query that would undo all of this
            val undoQueries = listOf(
                """
                    PREFIX n: <https://nap.nprod.net/>
                    DELETE {
                        ?update a n:update.
                        ?update n:name "20240609_species".
                    }
                    WHERE {
                        ?update a n:update.
                        ?update n:name "20240609_species".
                    }
                """.trimIndent(), """
                    PREFIX n: <https://nap.nprod.net/>
                    DELETE {
                        ?taxon ?p ?o.
                        ?organism n:taxon ?taxon.
                        ?taxon n:name ?name.
                    }
                    WHERE {
                        ?taxon a n:taxon.
                        ?taxon n:name ?name.
                        ?taxon ?p ?o.
                        OPTIONAL { ?organism n:taxon ?taxon. }
                    }
                """.trimIndent(), """
                    PREFIX n: <https://nap.nprod.net/>
                    DELETE {
                        ?s n:has_taxon ?o.
                    }
                    WHERE {
                        ?s n:has_taxon ?o.
                    }
                """.trimIndent()
            )

            undoQueries.forEach { undoQuery ->
                val arqQuery = UpdateFactory.create(undoQuery, Syntax.syntaxARQ)
                val qExec = UpdateExecutionFactory.create(arqQuery, dataset)
                qExec.execute()
            }
        }
    }
}
