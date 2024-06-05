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

fun pharmaciesOfTaxa(
    sparqlConnector: SparqlConnector,
    familyName: String?,
    genusName: String?,
    speciesName: String?
): List<Pharmacy> {
    var matcher = ""
    if ((familyName ?: "") != "")
        matcher += "?organism n:familyname \"$familyName\"."
    if ((genusName ?: "") != "")
        matcher += "?organism n:genusname \"$genusName\"."
    if ((speciesName ?: "") != "")
        matcher += "?organism n:speciesname \"$speciesName\"."

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
                ?organism n:organismclass ?organism_class.
                ?organism_class n:organismclass ?organism_class_name.
                ?compound a n:compound.
                ?compound n:name ?compoundName.
           } 
            WHERE {
              $matcher
              ?pharmacy a n:pharmacy;
                        n:has_worktype ?worktype.
                          
              ?organism n:familyname ?organism_family_name.
              ?organism n:genusname ?organism_genus_name.
              ?organism n:speciesname ?organism_species_name.
              ?pharmacy n:has_organism ?organism.
              OPTIONAL { ?pharmacy n:has_participant ?compound. 
                         ?compound a n:compound; 
                                  n:name ?compoundName.  }
              OPTIONAL { ?pharmacy n:has_pharmacology ?pharmacology.
                         ?pharmacology n:name ?pharmacologyName. }
              OPTIONAL { ?organism n:organismclass ?organism_class.
                         ?organism_class n:organismclass ?organism_class_name.
              }
            } LIMIT 10000
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
                          n:has_pharmacology ?pharmacology.
                ?pharmacology n:name ?pharmacologyName.
                ?organism n:familyname ?organism_family_name.
                ?organism n:genusname ?organism_genus_name.
                ?organism n:speciesname ?organism_species_name.
                ?organism n:organismclass ?organism_class.
                ?organism_class n:organismclass ?organism_class_name.
           } 
            WHERE {
              $matcher
              ?pharmacy a n:pharmacy;
                        n:has_worktype ?worktype.
                          
              OPTIONAL { ?pharmacy n:has_organism ?organism. }
              OPTIONAL { ?pharmacy n:has_pharmacology ?pharmacology.
                         ?pharmacology n:name ?pharmacologyName. }
              OPTIONAL { ?organism n:familyname ?organism_family_name. }
              OPTIONAL { ?organism n:genusname ?organism_genus_name. }
              OPTIONAL { ?organism n:speciesname ?organism_species_name. }
              OPTIONAL { ?organism n:organismclass ?organism_class.
                         ?organism_class n:organismclass ?organism_class_name.
              }
              $values
            } LIMIT 10000
        """.trimIndent()

    return pharmaciesFromQuery(sparqlConnector, query)
}