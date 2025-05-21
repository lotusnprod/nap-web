package net.nprod.nap.pages.country

import kotlinx.serialization.Serializable
import net.nprod.nap.types.Country
import net.nprod.nap.types.Pharmacy

/**
 * Data class to hold country information for the view
 */
@Serializable
data class CountryViewData(
    val identifier: String,
    val country: Country,
    val pharmacies: List<Pharmacy>
)