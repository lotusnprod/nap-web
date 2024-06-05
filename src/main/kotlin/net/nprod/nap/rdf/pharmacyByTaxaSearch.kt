package net.nprod.nap.rdf

import dataPage
import kotlinx.html.*
import net.nprod.nap.pages.invalidEntryPage
import net.nprod.nap.pages.presentPharmacyResults

val safeNameRegex = Regex("[^a-zA-Z-1-9.-]")

fun pharmacyByTaxaSearch(family: String?, genus: String?, species: String?): String {
    // if family contains anything else than alphanum dot and hyphen without a regex
    if (family?.contains(safeNameRegex) == true)
        return invalidEntryPage("family", family)
    if (genus?.contains(safeNameRegex) == true)
        return invalidEntryPage("genus", genus)
    if (species?.contains(safeNameRegex) == true)
        return invalidEntryPage("species", species)

    val sparqlConnector = SparqlConnector()

    val pharmacyResults = pharmaciesOfTaxa(sparqlConnector, family, genus, species)

    var title ="Experiments for $genus $species"
    if (family!=null) title+= " ($family)"

    return dataPage("") {
        id = "content-node"
        h1 { +title }

        if (pharmacyResults.size >= 10000) {
            +"We found more than 10000 experiments, please refine your search or use SPARQL queries"
        }
        else {
            +"We found ${pharmacyResults.size} experiments"
        }

        presentPharmacyResults(pharmacyResults, sourceType = "organism")
    }
}

