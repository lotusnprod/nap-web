package net.nprod.nap.rdf

import net.nprod.nap.types.Compound
import net.nprod.nap.types.CompoundSynonym
import net.nprod.nap.types.Pharmacy

fun pharmaciesOfCompound(
    uri: String,
    sparqlConnector: SparqlConnector
): List<Pharmacy> {
    val matcher = "?pharmacy n:has_participant <$uri>."
    val values = ""

    return pharmaciesGeneric(sparqlConnector, matcher, values)
}

fun pharmaciesOfPharmacology(
    uri: String,
    sparqlConnector: SparqlConnector
): List<Pharmacy> {
    val matcher = "?pharmacy n:has_pharmacology <$uri>."
    val values = ""

    return pharmaciesGeneric(sparqlConnector, matcher, values)
}

fun pharmaciesOfOrganism(
    uri: String,
    sparqlConnector: SparqlConnector
): List<Pharmacy> {
    val matcher = "?pharmacy n:has_organism <$uri>."
    val values = ""

    return pharmaciesGeneric(sparqlConnector, matcher, values)
}

fun pharmaciesOfWorktype(
    uri: String,
    sparqlConnector: SparqlConnector
): List<Pharmacy> {
    val matcher = "?pharmacy n:has_worktype <$uri>."
    val values = ""

    return pharmaciesGeneric(sparqlConnector, matcher, values)
}

fun pharmaciesOfAnimal(
    uri: String,
    sparqlConnector: SparqlConnector
): List<Pharmacy> {
    val matcher = "?pharmacy n:has_animal <$uri>."
    val values = ""

    return pharmaciesGeneric(sparqlConnector, matcher, values)
}

fun pharmaciesOfTaxa(
    sparqlConnector: SparqlConnector,
    taxonId: String,
): List<Pharmacy> {
    val query = """
            PREFIX n: <https://nap.nprod.net/>
            CONSTRUCT {
                ?pharmacy a n:pharmacy;
                          n:has_worktype ?worktype;
                          n:has_organism ?organism;
                          n:has_participant ?compound;
                          n:has_pharmacology ?pharmacology.
                ?pharmacology n:name ?pharmacologyName.
                ?organism n:familyname ?organism_family_name.
                ?organism n:genusname ?organism_genus_name.
                ?organism n:speciesname ?organism_species_name.
                ?organism n:has_taxon ?taxon.
                ?compound a n:compound.
                ?compound n:name ?compoundName.
           } 
            WHERE {
              ?organism a n:organism.
              ?pharmacy a n:pharmacy.
              ?pharmacy n:has_organism ?organism.
              ?pharmacy n:has_worktype ?worktype.
              ?organism n:has_taxon <https://nap.nprod.net/taxon/$taxonId>.
              OPTIONAL { ?organism n:familyname ?organism_family_name. }
              OPTIONAL { ?organism n:genusname ?organism_genus_name. }
              OPTIONAL { ?organism n:speciesname ?organism_species_name. }       
              OPTIONAL { ?pharmacy n:has_participant ?compound. 
                         ?compound a n:compound; 
                                  n:name ?compoundName.  }
              OPTIONAL { ?pharmacy n:has_pharmacology ?pharmacology. }
              OPTIONAL { ?organism n:organismclass ?organism_class. }
            }
        """.trimIndent()

    return pharmaciesFromQuery(sparqlConnector, query)
}

fun pharmaciesOfGeographicalArea(
    uri: String,
    sparqlConnector: SparqlConnector
): List<Pharmacy> {
    val matcher = """
            ?pharmacy n:has_organism ?organism.
            ?organism n:collectedFrom <$uri>.
            <$uri> a n:geographicalarea.
    """
    val values = ""

    return pharmaciesGeneric(sparqlConnector, matcher, values)
}

fun pharmaciesOfCountry(
    uri: String,
    sparqlConnector: SparqlConnector
): List<Pharmacy> {
    val matcher = """
            ?pharmacy n:has_organism ?organism.
            ?organism n:collectedFrom <$uri>.
            <$uri> a n:country.
    """
    val values = ""

    return pharmaciesGeneric(sparqlConnector, matcher, values)
}

fun pharmaciesOfPartCondition(
    uri: String,
    sparqlConnector: SparqlConnector
): List<Pharmacy> {
    val matcher = "?pharmacy n:has_collected_condition <$uri>."
    val values = ""

    return pharmaciesGeneric(sparqlConnector, matcher, values)
}

fun pharmaciesOfExtract(
    uri: String,
    sparqlConnector: SparqlConnector
): List<Pharmacy> {
    val matcher = "?pharmacy n:has_extract <$uri>."
    val values = ""

    return pharmaciesGeneric(sparqlConnector, matcher, values)
}

/**
 * Get all compounds with a specific compound code
 *
 * @param uri The URI of the compound code
 * @param sparqlConnector The SPARQL connector
 * @return A list of compounds with the given compound code
 */
fun compoundsByCompoundCode(
    uri: String,
    sparqlConnector: SparqlConnector
): List<Compound> {
    val sparql = """
        PREFIX n: <https://nap.nprod.net/>
        SELECT DISTINCT ?compound ?compoundName ?synonym ?synonymName
        WHERE {
            ?compound n:has_compoundcode <$uri>.
            ?compound a n:compound.
            ?compound n:name ?compoundName.
            OPTIONAL {
                ?compound n:has_synonym ?synonym.
                ?synonym n:name ?synonymName.
            }
        }
        ORDER BY ?compoundName
    """.trimIndent()
    
    val results = sparqlConnector.getResultsOfQuery(sparql)
    
    // Group by compound to properly associate synonyms
    val compoundMap = mutableMapOf<String, Compound>()
    
    if (results != null) {
        while (results.hasNext()) {
            val solution = results.nextSolution()
            val compoundUri = solution.getResource("compound")?.uri ?: continue
            val compoundName = solution.getLiteral("compoundName")?.string ?: continue
            
            // Get or create the compound
            val compound = compoundMap.getOrPut(compoundUri) {
                Compound(uri = compoundUri, name = compoundName)
            }
            
            // Add synonym if present
            val synonymResource = solution.getResource("synonym")
            val synonymLiteral = solution.getLiteral("synonymName")
            
            if (synonymResource != null && synonymLiteral != null) {
                val synonymUri = synonymResource.uri
                val synonymName = synonymLiteral.string
                val synonym = CompoundSynonym(synonymUri, synonymName)
                
                // Check if this synonym is already added
                if (compound.synonyms.none { it.uri == synonymUri }) {
                    compound.synonyms.add(synonym)
                }
            }
        }
    }
    
    return compoundMap.values.toList()
}

fun pharmaciesGeneric(
    sparqlConnector: SparqlConnector,
    matcher: String,
    values: String,
): List<Pharmacy> {
    // Somehow we can't have a naive CONSTRUCT with no block when we have optionals. That's annoying

    val query = """
            PREFIX n: <https://nap.nprod.net/>
            CONSTRUCT {
                $matcher
                ?pharmacy a n:pharmacy;
                          n:has_worktype ?worktype;
                          n:has_organism ?organism;
                          n:has_participant ?compound;
                          n:has_pharmacology ?pharmacology;
                          n:number ?number.
                ?pharmacology n:name ?pharmacologyName.
                ?organism n:familyname ?organism_family_name.
                ?organism n:genusname ?organism_genus_name.
                ?organism n:speciesname ?organism_species_name.
                ?compound a n:compound.
                ?compound n:name ?compoundName.
           } 
            WHERE {
              $matcher
              ?pharmacy a n:pharmacy;
                        n:has_worktype ?worktype.
                          
              OPTIONAL { ?pharmacy n:has_organism ?organism. }
              OPTIONAL { ?pharmacy n:has_pharmacology ?pharmacology.
                         ?pharmacology n:name ?pharmacologyName. }
              OPTIONAL { ?pharmacy n:has_participant ?compound. 
                         ?compound a n:compound; 
                                   n:name ?compoundName.  }
              OPTIONAL { ?organism n:familyname ?organism_family_name. }
              OPTIONAL { ?organism n:genusname ?organism_genus_name. }
              OPTIONAL { ?organism n:speciesname ?organism_species_name. }
              OPTIONAL { ?organism n:organismclass ?organism_class. }
              OPTIONAL { ?pharmacy n:number ?number. }
              $values
            } LIMIT 10000
        """.trimIndent()

    return pharmaciesFromQuery(sparqlConnector, query)
}