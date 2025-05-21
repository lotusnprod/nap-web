package net.nprod.nap.types

import kotlinx.serialization.Serializable

@Serializable
data class CompoundSynonym(
    var uri: String,
    var name: String
)