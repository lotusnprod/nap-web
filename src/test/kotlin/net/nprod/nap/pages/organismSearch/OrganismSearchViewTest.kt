package net.nprod.nap.pages.organismSearch

import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertFalse

class OrganismSearchViewTest {

    private fun row(displayName: String, recordedName: String) = mapOf(
        "uri" to "https://nap.nprod.net/organism/8001",
        "displayName" to displayName,
        "recordedName" to recordedName,
        "genusname" to "Adonis",
        "speciesname" to "aleppica",
        "subspeciesname" to "",
        "familyname" to "RANUNCULACEAE",
        "number" to "8001",
        "taxon" to "https://nap.nprod.net/taxon/8001"
    )

    @Test
    fun testRowIsNamedAfterTheTaxonItLinksTo() {
        val html = OrganismSearchView.render(
            OrganismSearchViewData("adonis", listOf(row("Achillea aleppica", "Adonis aleppica (RANUNCULACEAE)")))
        )

        // The heading of the destination page says the same thing, so the two agree
        assertContains(html, """<a href="/pharmacy_search?taxon_id=8001">Achillea aleppica</a>""")
        assertContains(html, "recorded as Adonis aleppica (RANUNCULACEAE)")
    }

    @Test
    fun testTheRecordedNameIsNotRepeatedWhenItIsTheSame() {
        val html = OrganismSearchView.render(
            OrganismSearchViewData("adonis", listOf(row("Adonis aleppica", "Adonis aleppica")))
        )

        assertFalse(html.contains("recorded as"), "there is nothing to reconcile when the names match")
    }
}
