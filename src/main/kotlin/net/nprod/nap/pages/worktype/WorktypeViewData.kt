package net.nprod.nap.pages.worktype

import kotlinx.serialization.Serializable
import net.nprod.nap.types.Pharmacy
import net.nprod.nap.types.Worktype

/**
 * Data class to hold worktype information for the view
 */
@Serializable
data class WorktypeViewData(
    val identifier: String,
    val worktype: Worktype,
    val pharmacyResults: List<Pharmacy>
)