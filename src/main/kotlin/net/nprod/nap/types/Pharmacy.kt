package net.nprod.nap.types

data class Pharmacy(
    val uri: String,
    val worktypes: MutableList<Worktype> = mutableListOf(),
    var organism: Organism? = null,
    var pharmacology: Pharmacology? = null,
    val compounds: MutableList<Compound> = mutableListOf()
)
