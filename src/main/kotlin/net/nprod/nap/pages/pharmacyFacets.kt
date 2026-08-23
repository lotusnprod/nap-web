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

/** Identifier used for experiments that have no value for a given facet */
const val FACET_NONE = "__none__"

/**
 * Compute the worktype facet values of a list of experiments, most frequent first
 */
fun worktypeFacetValues(pharmacyResults: List<Pharmacy>): List<FacetValue> {
    val counts = pharmacyResults
        .flatMap { pharmacy -> pharmacy.worktypes.map { it.uri.getRef() to it.name } }
        .groupingBy { it }
        .eachCount()
        .map { (key, count) -> FacetValue(key.first, key.second, count) }

    val withoutWorktype = pharmacyResults.count { it.worktypes.isEmpty() }
    return sortFacetValues(counts, withoutWorktype, "No worktype")
}

/**
 * Compute the pharmacology facet values of a list of experiments, most frequent first
 */
fun pharmacologyFacetValues(pharmacyResults: List<Pharmacy>): List<FacetValue> {
    val counts = pharmacyResults
        .mapNotNull { pharmacy -> pharmacy.pharmacology?.let { it.uri.getRef() to it.name } }
        .groupingBy { it }
        .eachCount()
        .map { (key, count) -> FacetValue(key.first, key.second, count) }

    val withoutPharmacology = pharmacyResults.count { it.pharmacology == null }
    return sortFacetValues(counts, withoutPharmacology, "No pharmacology")
}

private fun sortFacetValues(values: List<FacetValue>, noneCount: Int, noneLabel: String): List<FacetValue> {
    val sorted = values.sortedWith(compareByDescending<FacetValue> { it.count }.thenBy { it.label.lowercase() })
    return if (noneCount > 0) sorted + FacetValue(FACET_NONE, noneLabel, noneCount) else sorted
}

/**
 * Render the faceted filter panel for a list of experiments.
 * Filtering itself happens client side on the rows rendered by [presentPharmacyResults],
 * see [pharmacyFacetsScript].
 */
fun DIV.pharmacyFacets(pharmacyResults: List<Pharmacy>) {
    val worktypes = worktypeFacetValues(pharmacyResults)
    val pharmacologies = pharmacologyFacetValues(pharmacyResults)

    if (worktypes.isEmpty() && pharmacologies.isEmpty()) return

    div("card mb-4 facet-panel") {
        id = "pharmacy-facets"
        div("card-header d-flex justify-content-between align-items-center") {
            h5(classes = "mb-0") { +"Filters" }
            button(classes = "btn btn-sm btn-outline-secondary d-none") {
                id = "facet-clear-all"
                type = ButtonType.button
                +"Clear all"
            }
        }
        div("card-body") {
            facetGroup("worktype", "Worktypes", "bg-primary", worktypes)
            facetGroup("pharmacology", "Pharmacology", "bg-purple", pharmacologies)
        }
    }
}

private fun DIV.facetGroup(key: String, title: String, badgeClass: String, values: List<FacetValue>) {
    if (values.isEmpty()) return

    div("facet-group mb-3") {
        attributes["data-facet"] = key
        div("d-flex justify-content-between align-items-center mb-2") {
            h6(classes = "mb-0") {
                +title
                +" "
                span(classes = "text-muted fw-normal") { +"(${values.size})" }
            }
            button(classes = "btn btn-link btn-sm p-0 facet-clear d-none") {
                type = ButtonType.button
                +"clear"
            }
        }
        if (values.size > 8) {
            input(type = InputType.text, classes = "form-control form-control-sm mb-2 facet-search") {
                placeholder = "Filter ${title.lowercase()}…"
                attributes["autocomplete"] = "off"
            }
        }
        div("facet-options") {
            values.forEach { value ->
                val inputId = "facet-$key-${value.id}"
                div("form-check facet-option") {
                    attributes["data-value"] = value.id
                    attributes["data-label"] = value.label.lowercase()
                    input(type = InputType.checkBox, classes = "form-check-input") {
                        id = inputId
                        this.value = value.id
                    }
                    label(classes = "form-check-label") {
                        htmlFor = inputId
                        span(classes = "badge $badgeClass") { +value.label }
                    }
                    span(classes = "facet-count text-muted") { +value.count.toString() }
                }
            }
            div("text-muted small facet-empty d-none") { +"No match" }
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

