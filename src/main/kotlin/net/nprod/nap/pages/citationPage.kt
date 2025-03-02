package net.nprod.nap.pages

import as_local_link_if_dev
import defaultPage
import genURI
import net.nprod.nap.pages.invalidEntryPage
import kotlinx.html.*
import net.nprod.nap.rdf.SparqlConnector
import net.nprod.nap.types.Citation

fun citationPage(identifier: String?): String {
    if (identifier?.toIntOrNull() == null)
        return invalidEntryPage("citation", identifier ?: "null")

    // Generate URI
    val uri = genURI("citation", identifier)

    val sparqlConnector = SparqlConnector()
    val citation = Citation.fromSparql(sparqlConnector, uri)

    return defaultPage("Citation $identifier") {
        id = "content-node"
        h1 { +"Citation $identifier" }

        div(classes = "citation-details") {
            h2 { +"Citation Details" }
            
            table {
                tbody {
                    citation.title?.let {
                        tr {
                            td { +"Title" }
                            td { +it }
                        }
                    }
                    citation.authors?.let {
                        tr {
                            td { +"Authors" }
                            td { +it }
                        }
                    }
                    citation.year?.let {
                        tr {
                            td { +"Year" }
                            td { +it }
                        }
                    }
                    citation.journal?.let {
                        tr {
                            td { +"Journal" }
                            td { +it }
                        }
                    }
                    citation.volume?.let {
                        tr {
                            td { +"Volume" }
                            td { +it }
                        }
                    }
                    citation.issue?.let {
                        tr {
                            td { +"Issue" }
                            td { +it }
                        }
                    }
                    citation.pages?.let {
                        tr {
                            td { +"Pages" }
                            td { +it }
                        }
                    }
                    citation.doi?.let {
                        tr {
                            td { +"DOI" }
                            td { 
                                a(href = "https://doi.org/$it", target = "_blank") { +it }
                            }
                        }
                    }
                }
            }
        }

        // Query for associated organisms and pharmacies
        val queryRelated = """
            PREFIX n: <https://nap.nprod.net/>
            SELECT ?organism ?pharmacy ?genus ?species ?family
            WHERE {
                {
                    ?organism a n:organism ;
                              n:participatesIn <$uri> .
                    OPTIONAL { ?organism n:genusname ?genus . }
                    OPTIONAL { ?organism n:speciesname ?species . }
                    OPTIONAL { ?organism n:familyname ?family . }
                } UNION {
                    ?organism a n:organism ;
                              n:participatesIn <$uri> ;
                              n:participatesIn ?pharmacy .
                    ?pharmacy a n:pharmacy .
                    OPTIONAL { ?organism n:genusname ?genus . }
                    OPTIONAL { ?organism n:speciesname ?species . }
                    OPTIONAL { ?organism n:familyname ?family . }
                }
            }
        """.trimIndent()

        val result = sparqlConnector.getResultsOfQuery(queryRelated)
        val organisms = mutableMapOf<String, Triple<String?, String?, String?>>()
        val pharmacies = mutableSetOf<String>()

        if (result != null) {
            while (result.hasNext()) {
                val solution = result.nextSolution()
                
                solution["organism"]?.asResource()?.uri?.let { orgUri ->
                    if (!organisms.containsKey(orgUri)) {
                        val genus = solution["genus"]?.asLiteral()?.string
                        val species = solution["species"]?.asLiteral()?.string
                        val family = solution["family"]?.asLiteral()?.string
                        organisms[orgUri] = Triple(genus, species, family)
                    }
                }
                
                solution["pharmacy"]?.asResource()?.uri?.let { pharmUri ->
                    pharmacies.add(pharmUri)
                }
            }
        }

        if (organisms.isNotEmpty()) {
            h2 { +"Related Organisms" }
            ul {
                organisms.forEach { (uri, info) ->
                    li {
                        val (genus, species, family) = info
                        val name = mutableListOf<String>()
                        
                        if (!genus.isNullOrEmpty()) {
                            val capitalizedGenus = genus.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
                            name.add(capitalizedGenus)
                        }
                        if (!species.isNullOrEmpty()) {
                            name.add(species.lowercase())
                        }
                        if (!family.isNullOrEmpty()) {
                            name.add("(${family})")
                        }
                        
                        val displayName = if (name.isEmpty()) "Unknown organism" else name.joinToString(" ")
                        a(href = uri.as_local_link_if_dev) { +displayName }
                    }
                }
            }
        }

        if (pharmacies.isNotEmpty()) {
            h2 { +"Related Experiments" }
            ul {
                pharmacies.forEach { uri ->
                    val id = uri.substringAfterLast("/")
                    li {
                        a(href = uri.as_local_link_if_dev) { +"Experiment $id" }
                    }
                }
            }
        }
    }
}