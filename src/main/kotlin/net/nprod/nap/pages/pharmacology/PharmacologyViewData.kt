package net.nprod.nap.pages.pharmacology

import kotlinx.serialization.Serializable
import net.nprod.nap.types.Pharmacology
import net.nprod.nap.types.Pharmacy

/**
 * Data class to hold pharmacology information for the view
 */
@Serializable
data class PharmacologyViewData(
    val identifier: String,
    val pharmacology: Pharmacology,
    val pharmacyResults: List<Pharmacy>
)