package net.nprod.nap.pages.administrationRoute

import kotlinx.serialization.Serializable
import net.nprod.nap.types.AdministrationRoute
import net.nprod.nap.types.Pharmacy

/**
 * Data class to hold administration route information for the view
 */
@Serializable
data class AdministrationRouteViewData(
    val identifier: String,
    val administrationRoute: AdministrationRoute,
    val pharmacyResults: List<Pharmacy>
)