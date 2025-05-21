package net.nprod.nap.pages.partCondition

import kotlinx.serialization.Serializable
import net.nprod.nap.types.PartCondition
import net.nprod.nap.types.Pharmacy

/**
 * Data class to hold part condition information for the view
 */
@Serializable
data class PartConditionViewData(
    val identifier: String,
    val partCondition: PartCondition,
    val pharmacies: List<Pharmacy>
)