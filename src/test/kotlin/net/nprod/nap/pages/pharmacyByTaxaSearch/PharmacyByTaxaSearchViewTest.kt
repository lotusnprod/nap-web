package net.nprod.nap.pages.pharmacyByTaxaSearch

import kotlin.test.Test
import kotlin.test.assertContains

class PharmacyByTaxaSearchViewTest {

    @Test
    fun testHeadingNamesTheTaxon() {
        val html = PharmacyByTaxaSearchView.render(
            PharmacyByTaxaSearchViewData(taxonId = "8001", taxonName = "Achillea aleppica")
        )

        assertContains(html, "Experiments for taxon Achillea aleppica")
    }

    @Test
    fun testHeadingFallsBackToTheIdentifier() {
        val html = PharmacyByTaxaSearchView.render(PharmacyByTaxaSearchViewData(taxonId = "8002", taxonName = "8002"))

        assertContains(html, "Experiments for taxon 8002")
    }
}
