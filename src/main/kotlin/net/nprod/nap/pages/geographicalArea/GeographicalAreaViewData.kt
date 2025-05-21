package net.nprod.nap.pages.geographicalArea

import kotlinx.serialization.Serializable
import net.nprod.nap.types.GeographicalArea
import net.nprod.nap.types.Pharmacy

/**
 * Data class to hold geographical area information for the view
 */
@Serializable
data class GeographicalAreaViewData(
    val identifier: String,
    val geographicalArea: GeographicalArea,
    val pharmacies: List<Pharmacy>
)