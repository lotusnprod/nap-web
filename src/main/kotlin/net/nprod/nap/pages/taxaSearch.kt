fun generatePharmacyTaxaSearchURL(taxon: String): String {
    return "/pharmacy_search?taxon_id=${taxon.getRef()}"
}
