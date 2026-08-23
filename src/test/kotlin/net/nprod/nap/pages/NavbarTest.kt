package net.nprod.nap.pages

import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertFalse

class NavbarTest {

    private fun navbarHtml(): String = defaultPage("Navbar test")

    @Test
    fun testSearchTypesAreCompoundTaxonAndPharmacology() {
        val html = navbarHtml()

        assertContains(html, """<option value="compound">""")
        assertContains(html, """<option value="taxon">""")
        assertContains(html, """<option value="pharmacology">""")
    }

    @Test
    fun testOrganismIsNotOfferedAsASearchType() {
        // The name search returns one result per taxon, so it is labelled as a taxon search.
        val html = navbarHtml()

        assertFalse(html.contains("""<option value="organism">"""), "Organism should no longer be a search type")
        assertFalse(html.contains(">Organism<"), "The search type dropdown should not offer Organism")
    }

    @Test
    fun testFaqIsLinked() {
        val html = navbarHtml()

        assertContains(html, """href="/faq"""")
    }
}
