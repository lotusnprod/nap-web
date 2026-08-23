package net.nprod.nap.pages

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlinx.html.div
import kotlinx.html.stream.createHTML
import net.nprod.nap.types.*

class PharmacyFacetsTest {

    private fun worktype(number: Int, name: String) =
        Worktype(uri = "https://nap.nprod.net/worktype/$number", code = "W$number", name = name)

    private fun pharmacology(number: Int, name: String) =
        Pharmacology(uri = "https://nap.nprod.net/pharmacology/$number", name = name)

    private fun organism(number: Int, genus: String, species: String) =
        Organism(uri = "https://nap.nprod.net/organism/$number", genusname = genus, speciesname = species)

    private fun compound(number: Int, name: String) =
        Compound(uri = "https://nap.nprod.net/compound/$number", name = name)

    private fun pharmacy(
        number: Int,
        worktypes: Set<Worktype> = emptySet(),
        pharmacology: Pharmacology? = null,
        organism: Organism? = null,
        compounds: Set<Compound> = emptySet()
    ) = Pharmacy(
        uri = "https://nap.nprod.net/pharmacy/$number",
        worktypes = worktypes.toMutableSet(),
        pharmacology = pharmacology,
        organism = organism,
        compounds = compounds.toMutableSet()
    )

    @Test
    fun testValuesAreCountedAndSortedByFrequency() {
        val invitro = worktype(1, "In vitro")
        val invivo = worktype(2, "In vivo")
        val results = listOf(
            pharmacy(1, worktypes = setOf(invitro)),
            pharmacy(2, worktypes = setOf(invitro, invivo)),
            pharmacy(3, worktypes = setOf(invitro))
        )

        val worktypes = facetsOf(results).single { it.key == "worktype" }

        assertEquals(listOf("1", "2"), worktypes.values.map { it.id })
        assertEquals(listOf(3, 1), worktypes.values.map { it.count })
    }

    @Test
    fun testExperimentsWithoutValueGetTheirOwnBucket() {
        val results = listOf(
            pharmacy(1, pharmacology = pharmacology(10, "Antibacterial activity")),
            pharmacy(2)
        )

        val pharmacologies = facetsOf(results).single { it.key == "pharmacology" }
        val none = pharmacologies.values.single { it.id == FACET_NONE }

        assertEquals("No pharmacology", none.label)
        assertEquals(1, none.count)
    }

    @Test
    fun testFacetWithASingleValueIsDropped() {
        // Every experiment of a pharmacology page shares the same pharmacology,
        // filtering on it cannot narrow anything down
        val shared = pharmacology(10, "Antibacterial activity")
        val results = listOf(
            pharmacy(1, worktypes = setOf(worktype(1, "In vitro")), pharmacology = shared),
            pharmacy(2, worktypes = setOf(worktype(2, "In vivo")), pharmacology = shared)
        )

        val facets = facetsOf(results)

        assertNull(facets.find { it.key == "pharmacology" }, "A single valued facet is useless")
        assertEquals(listOf("worktype"), facets.map { it.key })
    }

    @Test
    fun testAllDimensionsAreOffered() {
        val results = listOf(
            pharmacy(
                1,
                worktypes = setOf(worktype(1, "In vitro")),
                pharmacology = pharmacology(10, "Antibacterial activity"),
                organism = organism(100, "Testus", "primus"),
                compounds = setOf(compound(1000, "Taxol"))
            ),
            pharmacy(
                2,
                worktypes = setOf(worktype(2, "In vivo")),
                pharmacology = pharmacology(11, "Antifungal activity"),
                organism = organism(101, "Testus", "secundus"),
                compounds = setOf(compound(1001, "Baccatin"))
            )
        )

        assertEquals(
            listOf("worktype", "pharmacology", "compound", "organism"),
            facetsOf(results).map { it.key }
        )
    }

    @Test
    fun testPanelIsNotRenderedWhenThereIsNothingToFilter() {
        val results = listOf(pharmacy(1, worktypes = setOf(worktype(1, "In vitro"))))

        val html = createHTML().div { pharmacyFacets(results) }

        assertTrue(!html.contains("pharmacy-facets"), "A single experiment needs no filters")
    }

    @Test
    fun testPanelExposesTheFacetsAndTheirValues() {
        val results = listOf(
            pharmacy(1, worktypes = setOf(worktype(1, "In vitro"))),
            pharmacy(2, worktypes = setOf(worktype(2, "In vivo")))
        )

        val html = createHTML().div { pharmacyFacets(results) }

        assertTrue(html.contains("""data-facet="worktype""""), "Should mark the facet group")
        assertTrue(html.contains("""id="facet-worktype-1""""), "Should offer the first worktype")
        assertTrue(html.contains("""id="facet-worktype-2""""), "Should offer the second worktype")
        assertTrue(html.contains("In vitro"), "Should label the values")
    }

    @Test
    fun testRowsCarryTheFacetValuesOfTheirExperiment() {
        val results = listOf(
            pharmacy(
                1,
                worktypes = setOf(worktype(1, "In vitro"), worktype(2, "In vivo")),
                pharmacology = pharmacology(10, "Antibacterial activity"),
                organism = organism(100, "Testus", "primus"),
                compounds = setOf(compound(1000, "Taxol"))
            )
        )

        val html = createHTML().div { presentPharmacyResults(results, "organism") }

        assertTrue(html.contains("""data-pharmacology="10""""), "Should carry the pharmacology")
        assertTrue(html.contains("""data-organism="100""""), "Should carry the organism")
        assertTrue(html.contains("""data-compound="1000""""), "Should carry the compounds")
        assertTrue(
            html.contains("""data-worktype="1|2"""") || html.contains("""data-worktype="2|1""""),
            "Should carry every worktype"
        )
    }
}
