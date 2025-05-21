package net.nprod.nap.pages.pharmacyByTaxaSearch

import net.nprod.nap.types.Pharmacy

/**
 * Data class for the pharmacy by taxa search page
 * @property taxonId The taxon ID to search for
 * @property pharmacyResults The list of pharmacy results for the taxon
 * @property organismName The display name of the organism
 */
data class PharmacyByTaxaSearchViewData(
    val taxonId: String,
    val pharmacyResults: List<Pharmacy> = emptyList(),
    val organismName: String = ""
)