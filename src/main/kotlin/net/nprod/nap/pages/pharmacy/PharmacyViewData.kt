package net.nprod.nap.pages.pharmacy

import kotlinx.serialization.Serializable
import net.nprod.nap.types.Pharmacy

/**
 * Data class to hold pharmacy information for the view
 */
@Serializable
data class PharmacyViewData(
    val identifier: String,
    val pharmacy: Pharmacy
)