package net.nprod.nap.pages.compoundSynonym

import kotlinx.serialization.Serializable
import net.nprod.nap.types.Compound
import net.nprod.nap.types.CompoundSynonym

/**
 * Data class to hold compound synonym information for the view
 */
@Serializable
data class CompoundSynonymViewData(
    val identifier: String,
    val synonym: CompoundSynonym,
    val compounds: List<Compound>
)