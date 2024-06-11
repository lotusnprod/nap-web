package net.nprod.nap.pages

import dataPage
import kotlinx.html.*
import net.nprod.nap.rdf.SparqlConnector
import net.nprod.nap.rdf.pharmaciesOfTaxa

val safeNameRegex = Regex("[^a-zA-Z-0-9.-]")

fun pharmacyByTaxaSearch(taxonId: String): String {
    if (taxonId.contains(safeNameRegex))
        return invalidEntryPage("taxon", taxonId)

    val sparqlConnector = SparqlConnector()

    val pharmacyResults = pharmaciesOfTaxa(sparqlConnector, taxonId)

    val name = pharmacyResults.firstOrNull()?.organism?.nameForHumans() ?: taxonId
    val title ="Experiments for taxon $name"

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

