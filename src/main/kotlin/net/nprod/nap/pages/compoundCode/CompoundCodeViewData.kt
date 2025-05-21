package net.nprod.nap.pages.compoundCode

import kotlinx.serialization.Serializable
import net.nprod.nap.types.Compound
import net.nprod.nap.types.CompoundCode

/**
 * Data class to hold compound code information for the view
 */
@Serializable
data class CompoundCodeViewData(
    val identifier: String,
    val compoundCode: CompoundCode,
    val compounds: List<Compound>
)