package net.nprod.nap.pages.organismSearch

/**
 * Data class for the organism search page
 * @property query The search query string
 * @property organisms The list of organisms matching the search query
 */
data class OrganismSearchViewData(
    val query: String? = null,
    val organisms: List<Map<String, String>> = emptyList()
)