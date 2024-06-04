package net.nprod.nap.types


import capitalize
import net.nprod.nap.rdf.SparqlConnector

data class Organism(
    val uri: String,
    var familyname: String? = null,
    var genusname: String? = null,
    var speciesname: String? = null,
    var collectedPart: CollectedPart? = null,
    var citation: Citation? = null,
    var organismClass: OrganismClass? = null,
    var speciesAuthority: String? = null,
    var subSpeciesAuthority: String? = null,
    var subSpeciesName: String? = null,
    var geographicalArea: GeographicalArea? = null,
    var country: Country? = null,
    var comment: String? = null,
    var collectedCondition: CollectedCondition? = null
) {

    fun nameForHumans(): String {
        val name_parts = mutableListOf<String>()
        genusname?.let { if (it != "") name_parts.add(it.lowercase().capitalize()) }
        speciesname?.let { if (it != "") name_parts.add(it.lowercase()) }
        familyname?.let { if (it != "") name_parts.add("(${it})") }
        if (name_parts.isEmpty()) return "Empty organism"
        return name_parts.joinToString(" ")
    }

    companion object {
        fun fromSparql(sparqlConnector: SparqlConnector, uri: String): Organism {
            var new: Organism? = null

            val query = """
            PREFIX n: <https://nap.nprod.net/>
            SELECT ?organism ?familyName ?genusName ?speciesName ?subSpeciesName ?collectedPart ?collectedPartName 
            ?comment
            ?citation
            ?country ?countryName ?geographicalarea ?geographicalareaName
            ?organismClass ?organismClassName ?collectedCondition ?collectedConditionName
            ?speciesAuthority ?subSpeciesAuthority
            {
                ?organism a n:organism;
                          n:participatesIn ?citation.
                ?citation a n:citation.
                ?organism n:organismclass ?organismClass.
                ?organismClass n:name ?organismClassName.
                OPTIONAL { ?organism n:familyname ?familyName. }
                OPTIONAL { ?organism n:genusname ?genusName. }
                OPTIONAL { ?organism n:speciesname ?speciesName. }
                OPTIONAL { ?organism n:speciesauthority ?speciesAuthority. }
                OPTIONAL { ?organism n:subspeciesauthority ?subSpeciesAuthority. }
                OPTIONAL { ?organism n:subspeciesname ?subSpeciesName. }
                OPTIONAL { ?organism n:collectedFrom ?geographicalarea. 
                            ?geographicalarea a n:geographicalarea  ;
                                              n:name ?geographicalareaName;
                }
                OPTIONAL { ?organism n:collectedFrom ?country. 
                            ?country a n:country; 
                                              n:name ?countryName;
                }
                
                
                OPTIONAL { ?organism n:comment ?comment. }
                OPTIONAL { ?organism n:has_collectedpart ?collectedPart. 
                           ?collectedPart n:name ?collectedPartName. }
                OPTIONAL { ?organism n:collectedCondition ?collectedCondition. 
                           ?collectedCondition n:name ?collectedConditionName. }
              
                VALUES ?organism { <$uri> }
            }
        """.trimIndent()

            val result = sparqlConnector.getResultsOfQuery(query)
            if (result != null) {
                while (result.hasNext()) {
                    val solution = result.nextSolution()
                    val organismUri = solution["organism"].asResource().uri
                    if (new == null) {
                        new = Organism(organismUri)
                        new.citation = Citation(solution["citation"].asResource().uri)
                        new.organismClass = OrganismClass(
                            solution["organismClass"].asResource().uri,
                            solution["organismClassName"].asLiteral().string
                        )
                        new.familyname = solution["familyName"]?.asLiteral()?.string
                        new.genusname = solution["genusName"]?.asLiteral()?.string
                        new.speciesname = solution["speciesName"]?.asLiteral()?.string
                        new.speciesAuthority = solution["speciesAuthority"]?.asLiteral()?.string
                        new.subSpeciesAuthority = solution["subSpeciesAuthority"]?.asLiteral()?.string
                        new.subSpeciesName = solution["subSpeciesName"]?.asLiteral()?.string
                        if (solution["collectedPart"] != null) {
                            new.collectedPart = CollectedPart(
                                solution["collectedPart"].asResource().uri,
                                solution["collectedPartName"]?.asLiteral()?.string ?: "Unknown collected part"
                            )
                        }
                        if (solution["geographicalarea"] != null) {
                            new.geographicalArea = GeographicalArea(
                                solution["geographicalarea"].asResource().uri,
                                solution["geographicalareaName"]?.asLiteral()?.string ?: "Unknown geographical area"
                            )
                        }
                        if (solution["country"] != null) {
                            new.country = Country(
                                solution["country"].asResource().uri,
                                solution["countryName"]?.asLiteral()?.string ?: "Unknown country"
                            )
                        }
                        new.comment = solution["comment"]?.asLiteral()?.string
                        if (solution["collectedCondition"] != null) {
                            new.collectedCondition = CollectedCondition(
                                solution["collectedCondition"].asResource().uri,
                                solution["collectedConditionName"]?.asLiteral()?.string ?: "Unknown collected condition"
                            )
                        }
                    }
                }
            }
            if (new == null) throw Exception("No organism found for $uri")
            return new
        }
    }
}
