package net.nprod.nap.pages.organism

import kotlinx.serialization.Serializable
import net.nprod.nap.types.Organism
import net.nprod.nap.types.Pharmacy

/**
 * Data class to hold organism information for the view
 */
@Serializable
data class OrganismViewData(
    val identifier: String,
    val organism: Organism,
    val pharmacyResults: List<Pharmacy>
)