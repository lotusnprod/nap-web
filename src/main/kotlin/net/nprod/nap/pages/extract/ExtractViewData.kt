package net.nprod.nap.pages.extract

import kotlinx.serialization.Serializable
import net.nprod.nap.types.Extract
import net.nprod.nap.types.Pharmacy

/**
 * Data class to hold extract information for the view
 */
@Serializable
data class ExtractViewData(
    val identifier: String,
    val extract: Extract,
    val pharmacyResults: List<Pharmacy>
)