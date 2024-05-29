package net.nprod.nap.types

data class Organism(
    val uri: String,
    var familyname: String? = null,
    var genusname: String? = null,
    var speciesname: String? = null,
    var collectedPart: CollectedPart? = null
) {
    fun nameForHumans(): String {
        val name_parts = mutableListOf<String>()
        genusname?.let { if (it!="") name_parts.add(it) }
        speciesname?.let { if (it!="") name_parts.add(it) }
        familyname?.let { if (it!="") name_parts.add("(${it})") }
        if (name_parts.isEmpty()) return "Empty organism"
        return name_parts.joinToString(" ")
    }
}