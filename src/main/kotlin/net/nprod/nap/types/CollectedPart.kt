package net.nprod.nap.types

import kotlinx.serialization.Serializable

@Serializable
data class CollectedPart (
    val uri: String,
    val name: String
)