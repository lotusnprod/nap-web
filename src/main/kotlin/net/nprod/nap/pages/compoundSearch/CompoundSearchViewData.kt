package net.nprod.nap.pages.compoundSearch

/**
 * Data class for the compound search page
 * @property query The search query string
 * @property compounds The list of compounds matching the search query
 */
data class CompoundSearchViewData(
    val query: String? = null,
    val compounds: List<Map<String, String>> = emptyList()
)