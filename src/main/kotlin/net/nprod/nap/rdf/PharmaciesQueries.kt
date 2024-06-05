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
    var hasFamily = false
    var hasGenus = false
    var hasSpecies = false
    if ((familyName ?: "") != "") {
        matcher += "VALUES ?organism_family_name { \"$familyName\" }."
        hasFamily = true
    }
    if ((genusName ?: "") != "") {
        matcher += "VALUES ?organism_genus_name { \"$genusName\" }."
        hasGenus = true
    }
    if ((speciesName ?: "") != "") {
        matcher += "VALUES ?organism_species_name { \"$speciesName\" }."
        hasSpecies = true
    }

   if (matcher == "") return emptyList()

    if (hasFamily) {
        matcher += "?organism n:familyname ?organism_family_name."
    } else {
        matcher += "OPTIONAL { ?organism n:familyname ?organism_family_name. }"
    }

    if (hasGenus) {
        matcher += "?organism n:genusname ?organism_genus_name."
    } else {
        matcher += "OPTIONAL { ?organism n:genusname ?organism_genus_name. }"
    }

    if (hasSpecies) {
        matcher += "?organism n:speciesname ?organism_species_name."
    } else {
        matcher += "OPTIONAL { ?organism n:speciesname ?organism_species_name. }"
    }

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
                ?compound a n:compound.
                ?compound n:name ?compoundName.
           } 
            WHERE {
              ?organism a n:organism.
              ?pharmacy a n:pharmacy.
              ?pharmacy n:has_organism ?organism.
              ?pharmacy n:has_worktype ?worktype.
              $matcher
                          
              OPTIONAL { ?pharmacy n:has_participant ?compound. 
                         ?compound a n:compound; 
                                  n:name ?compoundName.  }
              OPTIONAL { ?pharmacy n:has_pharmacology ?pharmacology. }
              OPTIONAL { ?organism n:organismclass ?organism_class. }
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
              OPTIONAL { ?organism n:organismclass ?organism_class. }
              $values
            } LIMIT 10000
        """.trimIndent()

    return pharmaciesFromQuery(sparqlConnector, query)
}