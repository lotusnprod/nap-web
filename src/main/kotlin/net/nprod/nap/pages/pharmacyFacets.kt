package net.nprod.nap.pages

import getRef
import kotlinx.html.*
import net.nprod.nap.types.Pharmacy

/**
 * A single selectable value of a facet
 *
 * @property id Stable identifier of the value (numeric reference of the URI, or "__none__")
 * @property label Human readable label
 * @property count Number of experiments carrying that value
 */
data class FacetValue(val id: String, val label: String, val count: Int)

/**
 * A facet: one dimension the experiments can be filtered on
 *
 * @property key Identifier of the facet, also the name of the row data attribute
 * @property title Title displayed above the values
 * @property kind The bubble kind used to display the values, so that they look like
 *                the same entities in the results table
 * @property values The values available for that facet, most frequent first
 * @property truncated Number of values that are not displayed
 */
data class Facet(
    val key: String,
    val title: String,
    val kind: BubbleKind,
    val values: List<FacetValue>,
    val truncated: Int = 0
)

/** Identifier used for experiments that have no value for a given facet */
const val FACET_NONE = "__none__"

/** Above that many values a facet is truncated, the list would be unusable anyway */
const val FACET_MAX_VALUES = 300

/**
 * Compute all the facets of a list of experiments. A facet with less than two values
 * cannot filter anything and is left out.
 */
fun facetsOf(pharmacyResults: List<Pharmacy>): List<Facet> {
    val facets = listOf(
        facetOf(
            "worktype", "Worktypes", BubbleKind.WORKTYPE, pharmacyResults, "No worktype"
        ) { pharmacy -> pharmacy.worktypes.map { it.uri.getRef() to it.name } },
        facetOf(
            "pharmacology", "Pharmacology", BubbleKind.PHARMACOLOGY, pharmacyResults, "No pharmacology"
        ) { pharmacy -> pharmacy.pharmacology?.let { listOf(it.uri.getRef() to it.name) } ?: emptyList() },
        facetOf(
            "compound", "Compounds", BubbleKind.COMPOUND, pharmacyResults, "No compound"
        ) { pharmacy -> pharmacy.compounds.map { it.uri.getRef() to (it.name ?: "Unknown compound") } },
        facetOf(
            "organism", "Organisms", BubbleKind.ORGANISM, pharmacyResults, "No organism"
        ) { pharmacy -> pharmacy.organism?.let { listOf(it.uri.getRef() to it.nameForHumans()) } ?: emptyList() }
    )

    return facets.filter { it.values.size > 1 }
}

/**
 * Build one facet by extracting its values from every experiment
 *
 * @param noneLabel Label of the bucket holding the experiments that have no value
 * @param valuesOf Extracts the (id, label) pairs of an experiment for that facet
 */
private fun facetOf(
    key: String,
    title: String,
    kind: BubbleKind,
    pharmacyResults: List<Pharmacy>,
    noneLabel: String,
    valuesOf: (Pharmacy) -> List<Pair<String, String>>
): Facet {
    val counts = pharmacyResults
        .flatMap { valuesOf(it).distinct() }
        .groupingBy { it }
        .eachCount()
        .map { (value, count) -> FacetValue(value.first, value.second, count) }
        .sortedWith(compareByDescending<FacetValue> { it.count }.thenBy { it.label.lowercase() })

    val withoutValue = pharmacyResults.count { valuesOf(it).isEmpty() }

    val kept = counts.take(FACET_MAX_VALUES)
    val values = if (withoutValue > 0) kept + FacetValue(FACET_NONE, noneLabel, withoutValue) else kept

    return Facet(key, title, kind, values, truncated = counts.size - kept.size)
}

/**
 * Render the faceted filter panel of a list of experiments, as the sidebar of a
 * `facet-layout`. Nothing is rendered when there is nothing to filter on.
 * Filtering itself happens client side on the rows rendered by [presentPharmacyResults],
 * see [pharmacyFacetsScript].
 */
fun DIV.pharmacyFacets(pharmacyResults: List<Pharmacy>) {
    val facets = facetsOf(pharmacyResults)
    if (facets.isEmpty() || pharmacyResults.size < 2) return

    div("facet-sidebar") {
        div("card mb-3 facet-panel") {
            id = "pharmacy-facets"
            tourStep(
                TourStep.FACETS,
                "Narrow the results",
                "Experiment lists get long. Ticking values here filters the table without reloading the page, and " +
                    "the counts tell you how much is left."
            )
            div("card-header d-flex justify-content-between align-items-center") {
                h5(classes = "mb-0") { +"Filters" }
                button(classes = "btn btn-sm btn-outline-secondary d-none") {
                    id = "facet-clear-all"
                    type = ButtonType.button
                    +"Clear all"
                }
            }
            div("card-body") {
                facets.forEach { facetGroup(it) }
            }
        }
    }
}

private fun DIV.facetGroup(facet: Facet) {
    div("facet-group mb-3") {
        attributes["data-facet"] = facet.key
        div("d-flex justify-content-between align-items-center mb-2") {
            h6(classes = "mb-0") {
                +facet.title
                +" "
                span(classes = "text-muted fw-normal") { +"(${facet.values.size})" }
            }
            button(classes = "btn btn-link btn-sm p-0 facet-clear d-none") {
                type = ButtonType.button
                +"clear"
            }
        }
        if (facet.values.size > 8) {
            input(type = InputType.text, classes = "form-control form-control-sm mb-2 facet-search") {
                placeholder = "Filter ${facet.title.lowercase()}…"
                attributes["autocomplete"] = "off"
            }
        }
        div("facet-options") {
            facet.values.forEach { value ->
                val inputId = "facet-${facet.key}-${value.id}"
                div("form-check facet-option") {
                    attributes["data-value"] = value.id
                    attributes["data-label"] = value.label.lowercase()
                    input(type = InputType.checkBox, classes = "form-check-input") {
                        id = inputId
                        this.value = value.id
                    }
                    label(classes = "form-check-label") {
                        htmlFor = inputId
                        bubble(facet.kind, value.label)
                    }
                    span(classes = "facet-count text-muted") { +value.count.toString() }
                }
            }
            div("text-muted small facet-empty d-none") { +"No match" }
        }
        if (facet.truncated > 0) {
            div("text-muted small mt-1") { +"${facet.truncated} rarer values not listed" }
        }
    }
}

/**
 * Load the client side logic of the faceted search: it filters the experiment rows,
 * recomputes the counts of the values that are still reachable, and keeps the selection
 * in the URL query string.
 */
fun DIV.pharmacyFacetsScript() {
    script(src = "/assets/js/pharmacy-facets.js") {
        defer = true
    }
}
