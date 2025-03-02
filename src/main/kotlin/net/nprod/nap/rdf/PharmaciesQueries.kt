package net.nprod.nap.rdf

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
                          n:has_pharmacology ?pharmacology.
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
              $values
            } LIMIT 10000
        """.trimIndent()

    return pharmaciesFromQuery(sparqlConnector, query)
}