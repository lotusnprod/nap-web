package net.nprod.nap.pages.citation

import kotlinx.html.*
import net.nprod.nap.helpers.localLinks
import net.nprod.nap.pages.defaultPage

/**
 * View for rendering citation page
 */
object CitationView {
    /**
     * Renders the citation page
     * 
     * @param data The citation data to render
     * @return HTML string representation of the page
     */
    fun render(data: CitationViewData): String {
        val citation = data.citation
        val identifier = data.identifier
        val formattedCitation = data.formattedCitation
        val organismData = data.organismData
        
        // Citation title - either from actual title or a formatted citation
        val pageTitle = citation.title ?: "Citation $identifier"
        
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

                // Organisms and experiments section within the container
                div("row") {
                    div("col-12") {
                        if (organismData.isNotEmpty()) {
                            div("card mb-4") {
                                id = "organisms"
                                div("card-header bg-primary text-white") {
                                    h3(classes = "card-title mb-0") {
                                        +"Organisms and Their Experiments (${organismData.size})"
                                    }
                                }
                                div("card-body p-0") {
                                    organismData.forEach { organism ->
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
                                                    href = localLinks(organism.uri),
                                                    classes = "text-decoration-none"
                                                ) { +displayName }
                                            }

                                            if (organism.experiments.isNotEmpty()) {
                                                // Sort experiments by their number value
                                                val sortedExperiments = organism.experiments.sortedBy {
                                                    it.number?.toDoubleOrNull() ?: Double.MAX_VALUE
                                                }

                                                div("table-responsive") {
                                                    table(classes = "table table-striped table-hover table-sm") {
                                                        thead {
                                                            tr {
                                                                th(scope = ThScope.col) { +"Experiment #" }
                                                                th(scope = ThScope.col) { +"Pharmacology Type" }
                                                                th(scope = ThScope.col) { +"Worktypes" }
                                                                th(scope = ThScope.col) { +"Compound" }
                                                                th(scope = ThScope.col) { +"Results" }
                                                            }
                                                        }
                                                        tbody {
                                                            sortedExperiments.forEach { experiment ->
                                                                tr {
                                                                    td {
                                                                        a(
                                                                            href = localLinks(experiment.uri),
                                                                            classes = "text-decoration-none"
                                                                        ) {
                                                                            +(experiment.number ?: "N/A")
                                                                        }
                                                                    }
                                                                    td { +(experiment.pharmacologyName ?: "Not specified") }
                                                                    td {
                                                                        if (experiment.worktypes.isNotEmpty()) {
                                                                            div("d-flex flex-wrap gap-1") {
                                                                                experiment.worktypes.forEach { worktype ->
                                                                                    span(classes = "badge bg-primary me-1 mb-1 small") {
                                                                                        a(
                                                                                            href = localLinks(worktype.uri),
                                                                                            classes = "text-white text-decoration-none"
                                                                                        ) {
                                                                                            +worktype.name
                                                                                        }
                                                                                    }
                                                                                }
                                                                            }
                                                                        } else {
                                                                            +"None"
                                                                        }
                                                                    }
                                                                    td {
                                                                        if (experiment.compounds.isNotEmpty()) {
                                                                            div("d-flex flex-wrap gap-1") {
                                                                                experiment.compounds.forEach { compound ->
                                                                                    span(classes = "badge bg-success me-1 mb-1 small") {
                                                                                        a(
                                                                                            href = localLinks(compound.uri),
                                                                                            classes = "text-white text-decoration-none"
                                                                                        ) {
                                                                                            +compound.name
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
                                                                                experiment.qualitativeResults.forEach { result ->
                                                                                    span(classes = "badge bg-success me-1 mb-1 small") {
                                                                                        +result.name
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
}