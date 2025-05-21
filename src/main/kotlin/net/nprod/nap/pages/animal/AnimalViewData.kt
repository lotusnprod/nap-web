package net.nprod.nap.pages.animal

import kotlinx.serialization.Serializable
import net.nprod.nap.types.Animal
import net.nprod.nap.types.Pharmacy

/**
 * Data class to hold animal information for the view
 */
@Serializable
data class AnimalViewData(
    val identifier: String,
    val animal: Animal,
    val pharmacyResults: List<Pharmacy>
)