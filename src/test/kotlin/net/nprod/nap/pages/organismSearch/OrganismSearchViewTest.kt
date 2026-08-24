package net.nprod.nap.pages.organismSearch

import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertFalse

class OrganismSearchViewTest {

    private fun row(
        genus: String = "ADONIS",
        species: String = "ALEPPICA",
        subspecies: String = "",
        taxonName: String = "ALEPPICA",
        family: String = "RANUNCULACEAE",
        organismClass: String = "DICOT",
        experiments: String = "21"
    ) = mapOf(
        "uri" to "https://nap.nprod.net/organism/8001",
        "taxonName" to taxonName,
        "recordedName" to "Organism #8001",
        "genusname" to genus,
        "speciesname" to species,
        "subspeciesname" to subspecies,
        "familyname" to family,
        "organismClass" to organismClass,
        "experiments" to experiments,
        "number" to "8001",
        "taxon" to "https://nap.nprod.net/taxon/8001"
    )

    private fun render(vararg rows: Map<String, String>) =
        OrganismSearchView.render(OrganismSearchViewData("adonis", rows.toList()))

    @Test
    fun testEveryRankHasItsOwnColumn() {
        // The name used to be glued into one cell, with Subspecies repeating part of it
        val html = render(row())

        listOf("Genus", "Species", "Subspecies", "Family", "Type", "Experiments").forEach {
            // Not "<th>...", the count column is right-aligned and so carries a class
            assertContains(html, ">$it</th>", message = "expected a $it column")
        }
    }

    @Test
    fun testARowShowsWhatWasFoundAndHowMuchOfIt() {
        val html = render(row(experiments = "21"))

        assertContains(html, "<td>ADONIS</td>")
        assertContains(html, "RANUNCULACEAE")
        assertContains(html, "DICOT")
        // The count is what tells you which of two hundred taxa is worth following
        assertContains(html, "21")
    }

    @Test
    fun testTheSpeciesIsTheLinkToTheExperiments() {
        val html = render(row())

        assertContains(html, """<a href="/pharmacy_search?taxon_id=8001">ALEPPICA</a>""")
    }

    @Test
    fun testTheGenusCarriesTheLinkWhenThereIsNoSpecies() {
        // Otherwise a genus-only record would have nothing to click
        val html = render(row(species = "", taxonName = "ADONIS"))

        assertContains(html, """<a href="/pharmacy_search?taxon_id=8001">ADONIS</a>""")
    }

    @Test
    fun testTheTaxonNameIsNotRepeatedWhenAColumnAlreadyShowsIt() {
        // A taxon is named after one epithet: the species for a species, the subspecies
        // for a subspecies. Both are columns, so saying it again is noise.
        assertFalse(render(row(taxonName = "ALEPPICA")).contains("taxon:"))
        assertFalse(
            render(row(subspecies = "CV.CARDINALIS", taxonName = "CV.CARDINALIS")).contains("taxon:"),
            "the subspecies column already says it"
        )
    }

    @Test
    fun testTheTaxonNameIsShownWhenItIsNeitherRank() {
        val html = render(row(taxonName = "ACHILLEA ALEPPICA"))

        assertContains(html, "taxon: ACHILLEA ALEPPICA")
    }
}
