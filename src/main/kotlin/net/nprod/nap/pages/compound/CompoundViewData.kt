package net.nprod.nap.pages.compound

import kotlinx.serialization.Serializable
import net.nprod.nap.types.Compound
import net.nprod.nap.types.Pharmacy

/**
 * Data class to hold compound information for the view
 */
@Serializable
data class CompoundViewData(
    val identifier: String,
    val compound: Compound,
    val pharmacies: List<Pharmacy>
)