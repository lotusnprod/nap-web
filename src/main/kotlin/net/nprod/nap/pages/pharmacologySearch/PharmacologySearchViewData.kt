package net.nprod.nap.pages.pharmacologySearch

/**
 * Data class for the pharmacology search page
 * @property query The search query string
 * @property pharmacologyEntries The list of pharmacology entries matching the search query
 */
data class PharmacologySearchViewData(
    val query: String? = null,
    val pharmacologyEntries: List<Map<String, String>> = emptyList()
)