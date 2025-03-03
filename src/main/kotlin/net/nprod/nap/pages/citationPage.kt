package net.nprod.nap.pages

import as_local_link_if_dev
import defaultPage
import genURI
import kotlinx.html.*
import net.nprod.nap.rdf.SparqlConnector
import net.nprod.nap.types.Citation
import net.nprod.nap.types.QualitativeResult
import net.nprod.nap.types.Worktype

fun citationPage(identifier: String?): String {
    if (identifier?.toIntOrNull() == null)
        return invalidEntryPage("citation", identifier ?: "null")

    // Generate URI
    val uri = genURI("citation", identifier)

    val sparqlConnector = SparqlConnector()
    val citation = Citation.fromSparql(sparqlConnector, uri)

    // Citation title - either from actual title or a formatted citation
    val pageTitle = citation.title ?: "Citation $identifier"
    val formattedCitation = citation.formatCitation()

    return defaultPage(pageTitle) {
        id = "content-node"
        div("container") {
            div("row") {
                div("col-12") {
                    h1(classes = "mt-4 mb-4") { +"Citation $identifier" }
                    if (formattedCitation.isNotBlank()) {
                        p(classes = "lead") { +formattedCitation }
                    }
                }
            }

            div("row") {
                div("col-8") {
                    div("card mb-4") {
                        div("card-header bg-success text-white") {
                            h3(classes = "card-title mb-0") { +"Citation Details" }
                        }
                        div("card-body") {
                            div("table-responsive") {
                                table(classes = "table table-striped table-hover") {
                                    tbody {
                                        citation.title?.let {
                                            tr {
                                                th(scope = ThScope.row, classes = "w-25") { +"Title" }
                                                td { +it }
                                            }
                                        }
                                        citation.authors?.let {
                                            tr {
                                                th(scope = ThScope.row) { +"Authors" }
                                                td { +it }
                                            }
                                        }
                                        citation.year?.let {
                                            tr {
                                                th(scope = ThScope.row) { +"Year" }
                                                td { +it }
                                            }
                                        }
                                        citation.journal?.let {
                                            tr {
                                                th(scope = ThScope.row) { +"Journal" }
                                                td { +it }
                                            }
                                        }
                                        citation.volume?.let {
                                            tr {
                                                th(scope = ThScope.row) { +"Volume" }
                                                td { +it }
                                            }
                                        }
                                        citation.issue?.let {
                                            tr {
                                                th(scope = ThScope.row) { +"Issue" }
                                                td { +it }
                                            }
                                        }
                                        citation.pages?.let {
                                            tr {
                                                th(scope = ThScope.row) { +"Pages" }
                                                td { +it }
                                            }
                                        }
                                        citation.doi?.let {
                                            tr {
                                                th(scope = ThScope.row) { +"DOI" }
                                                td {
                                                    a(href = "https://doi.org/$it", target = "_blank") { +it }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                div("col-4") {
                    div("card mb-4") {
                        div("card-header bg-secondary text-white") {
                            h3(classes = "card-title mb-0") { +"Summary" }
                        }
                        div("card-body") {
                            p { +"Year: ${citation.year ?: "Unknown"}" }
                            p { +"Journal: ${citation.journal ?: "Unknown"}" }
                            if (!citation.doi.isNullOrBlank()) {
                                hr {}
                                div("d-grid gap-2") {
                                    a(
                                        href = "https://doi.org/${citation.doi}",
                                        target = "_blank",
                                        classes = "btn btn-primary"
                                    ) {
                                        +"View on DOI.org"
                                    }
                                }
                            }
                        }
                    }
                }
            }


            // Query for organisms, their pharmacology experiments, and experiment numbers from this citation
            val queryRelated = """
            PREFIX n: <https://nap.nprod.net/>
            SELECT ?organism ?pharmacy ?genus ?species ?family ?number ?pharmacology ?pharmacology_name ?worktype ?qualitativeResult
            WHERE {
                ?organism a n:organism ;
                          n:participatesIn <$uri>.
                ?pharmacy n:has_organism ?organism ;
                          n:number ?number .
                OPTIONAL { ?pharmacy n:has_pharmacology ?pharmacology .
                           ?pharmacology n:name ?pharmacology_name . }
                OPTIONAL { ?pharmacy n:has_worktype ?worktype . }
                OPTIONAL { ?pharmacy n:has_qualitativeresult ?qualitativeResult . }
                OPTIONAL { ?organism n:genusname ?genus . }
                OPTIONAL { ?organism n:speciesname ?species . }
                OPTIONAL { ?organism n:familyname ?family . }
            }
            ORDER BY ?organism ?number
        """.trimIndent()

            val result = sparqlConnector.getResultsOfQuery(queryRelated)

            // Structure to organize data by organism
            data class ExperimentInfo(
                val uri: String,
                val number: String?,
                val pharmacologyName: String?,
                val worktypes: MutableSet<String> = mutableSetOf(),
                val qualitativeResults: MutableSet<String> = mutableSetOf()
            )

            data class OrganismInfo(
                val uri: String,
                val genus: String?,
                val species: String?,
                val family: String?,
                val experiments: MutableMap<String, ExperimentInfo> = mutableMapOf()
            )

            val organismMap = mutableMapOf<String, OrganismInfo>()

            if (result != null) {
                while (result.hasNext()) {
                    val solution = result.nextSolution()

                    val organismUri = solution["organism"]?.asResource()?.uri ?: continue
                    val pharmacyUri = solution["pharmacy"]?.asResource()?.uri ?: continue
                    val number = solution["number"]?.asLiteral()?.string
                    val pharmacologyUri = solution["pharmacology"]?.asResource()?.uri
                    val pharmacologyName = solution["pharmacology_name"]?.asLiteral()?.string
                    val worktypeUri = solution["worktype"]?.asResource()?.uri
                    val qualitativeResultUri = solution["qualitativeResult"]?.asResource()?.uri

                    // Initialize organism if not exists
                    if (!organismMap.containsKey(organismUri)) {
                        val genus = solution["genus"]?.asLiteral()?.string
                        val species = solution["species"]?.asLiteral()?.string
                        val family = solution["family"]?.asLiteral()?.string
                        organismMap[organismUri] = OrganismInfo(organismUri, genus, species, family)
                    }

                    // Initialize or update experiment
                    val experimentInfo = organismMap[organismUri]?.experiments?.getOrPut(pharmacyUri) {
                        ExperimentInfo(uri = pharmacyUri, number = number, pharmacologyName = pharmacologyName)
                    }

                    // Add worktype if available
                    if (worktypeUri != null) {
                        experimentInfo?.worktypes?.add(worktypeUri)
                    }

                    // Add qualitative result if available
                    if (qualitativeResultUri != null) {
                        experimentInfo?.qualitativeResults?.add(qualitativeResultUri)
                    }
                }
            }

            // Organisms and experiments section within the container
            div("row") {
                div("col-12") {
                    if (organismMap.isNotEmpty()) {
                        div("card mb-4") {
                            id = "organisms"
                            div("card-header bg-primary text-white") {
                                h3(classes = "card-title mb-0") {
                                    +"Organisms and Their Experiments (${organismMap.size})"
                                }
                            }
                            div("card-body p-0") {
                                organismMap.forEach { (_, organism) ->
                                    div(classes = "mb-4 px-3 pt-3") {
                                        // Format organism name
                                        val name = mutableListOf<String>()
                                        if (!organism.genus.isNullOrEmpty()) {
                                            val capitalizedGenus = organism.genus.lowercase().replaceFirstChar {
                                                it.titlecase()
                                            }
                                            name.add(capitalizedGenus)
                                        }
                                        if (!organism.species.isNullOrEmpty()) {
                                            name.add(organism.species.lowercase())
                                        }
                                        if (!organism.family.isNullOrEmpty()) {
                                            name.add("(${organism.family})")
                                        }

                                        val displayName =
                                            if (name.isEmpty()) "Unknown organism" else name.joinToString(" ")

                                        h4(classes = "mt-2") {
                                            a(
                                                href = organism.uri.as_local_link_if_dev,
                                                classes = "text-decoration-none"
                                            ) { +displayName }
                                        }

                                        if (organism.experiments.isNotEmpty()) {
                                            // Sort experiments by their number value
                                            val sortedExperiments = organism.experiments.values.sortedBy {
                                                it.number?.toDoubleOrNull() ?: Double.MAX_VALUE
                                            }

                                            div("table-responsive") {
                                                table(classes = "table table-striped table-hover table-sm") {
                                                    thead {
                                                        tr {
                                                            th(scope = ThScope.col) { +"Experiment #" }
                                                            th(scope = ThScope.col) { +"Pharmacology Type" }
                                                            th(scope = ThScope.col) { +"Worktypes" }
                                                            th(scope = ThScope.col) { +"Results" }
                                                        }
                                                    }
                                                    tbody {
                                                        sortedExperiments.forEach { experiment ->
                                                            tr {
                                                                td {
                                                                    a(
                                                                        href = experiment.uri.as_local_link_if_dev,
                                                                        classes = "text-decoration-none"
                                                                    ) {
                                                                        +"${experiment.number ?: "N/A"}"
                                                                    }
                                                                }
                                                                td { +(experiment.pharmacologyName ?: "Not specified") }
                                                                td {
                                                                    if (experiment.worktypes.isNotEmpty()) {
                                                                        div("d-flex flex-wrap gap-1") {
                                                                            experiment.worktypes.forEach { worktypeUri ->
                                                                                val worktype =
                                                                                    Worktype.Cache[worktypeUri]
                                                                                if (worktype != null) {
                                                                                    span(classes = "badge bg-primary me-1 mb-1 small") {
                                                                                        a(
                                                                                            href = worktype.uri.as_local_link_if_dev,
                                                                                            classes = "text-white text-decoration-none"
                                                                                        ) {
                                                                                            +worktype.name
                                                                                        }
                                                                                    }
                                                                                }
                                                                            }
                                                                        }
                                                                    } else {
                                                                        +"None"
                                                                    }
                                                                }
                                                                td {
                                                                    if (experiment.qualitativeResults.isNotEmpty()) {
                                                                        div("d-flex flex-wrap gap-1") {
                                                                            experiment.qualitativeResults.forEach { resultUri ->
                                                                                val result =
                                                                                    QualitativeResult.Cache[resultUri]
                                                                                if (result != null) {
                                                                                    span(classes = "badge bg-success me-1 mb-1 small") {
                                                                                        +result.name
                                                                                    }
                                                                                }
                                                                            }
                                                                        }
                                                                    } else {
                                                                        +"None"
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        } else {
                                            div("alert alert-info mx-3 mb-3") { +"No experiments available for this organism." }
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        div("alert alert-warning mt-4 mx-3") {
                            +"No organisms or experiments found for this citation."
                        }
                    }
                }
            }
        }
    }
}