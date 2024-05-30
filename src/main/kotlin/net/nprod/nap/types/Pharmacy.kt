package net.nprod.nap.types

data class Pharmacy(
    val uri: String,
    val worktypes: MutableList<Worktype> = mutableListOf(),
    val organisms: MutableList<Organism> = mutableListOf(),
    val pharmacologies: MutableList<Pharmacology> = mutableListOf()
)
