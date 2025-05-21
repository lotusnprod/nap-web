package net.nprod.nap.pages.invalidEntry

import kotlinx.serialization.Serializable

/**
 * Data class to hold invalid entry information for the view
 */
@Serializable
data class InvalidEntryViewData(
    val type: String,
    val value: String
)