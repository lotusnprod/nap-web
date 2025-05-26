package net.nprod.nap.pages

import kotlin.test.Test
import kotlin.test.assertTrue
import kotlinx.html.*
import kotlinx.html.stream.createHTML
import net.nprod.nap.types.*

class PresentPharmacyResultsTest {

    @Test
    fun testPresentPharmacyResultsWithEmptyList() {
        val html = createHTML().div {
            presentPharmacyResults(emptyList(), "compound")
        }
        
        // Should contain the empty alert message
        assertTrue(html.contains("No experiments found"), "Should show empty message")
        assertTrue(html.contains("alert alert-info"), "Should have alert styling")
    }

    @Test
    fun testPresentPharmacyResultsWithData() {
        // Create minimal test data
        val pharmacy = Pharmacy(
            uri = "https://nap.nprod.net/pharmacy/1",
            worktypes = mutableSetOf(
                Worktype(uri = "https://nap.nprod.net/worktype/1", code = "TEST", name = "Test Worktype")
            ),
            pharmacology = Pharmacology(uri = "https://nap.nprod.net/pharmacology/1", name = "Test Pharmacology"),
            organism = Organism(
                uri = "https://nap.nprod.net/organism/1",
                genusname = "Testus",
                speciesname = "testicus"
            ),
            compounds = mutableSetOf(
                Compound(uri = "https://nap.nprod.net/compound/1", name = "Test Compound")
            )
        )
        
        val html = createHTML().div {
            presentPharmacyResults(listOf(pharmacy), "organism")
        }
        
        // Should contain the table structure
        assertTrue(html.contains("table table-striped"), "Should have table styling")
        assertTrue(html.contains("Test Worktype"), "Should contain worktype name")
        assertTrue(html.contains("Test Pharmacology"), "Should contain pharmacology name")
        assertTrue(html.contains("Testus testicus"), "Should contain organism name")
        assertTrue(html.contains("Test Compound"), "Should contain compound name")
    }

    @Test
    fun testPresentPharmacyResultsWithCompoundSourceType() {
        val pharmacy = Pharmacy(
            uri = "https://nap.nprod.net/pharmacy/1",
            worktypes = mutableSetOf(),
            pharmacology = null,
            organism = null,
            compounds = mutableSetOf()
        )
        
        val html = createHTML().div {
            presentPharmacyResults(listOf(pharmacy), "compound")
        }
        
        // When sourceType is "compound", should not include compound column
        // Just verify it doesn't contain "Compound" header
        assertTrue(!html.contains(">Compound<"), "Should not have Compound column when sourceType is compound")
    }

    @Test
    fun testPresentPharmacyResultsWithNonCompoundSourceType() {
        val pharmacy = Pharmacy(
            uri = "https://nap.nprod.net/pharmacy/1",
            worktypes = mutableSetOf(),
            pharmacology = null,
            organism = null,
            compounds = mutableSetOf()
        )
        
        val html = createHTML().div {
            presentPharmacyResults(listOf(pharmacy), "organism")
        }
        
        // When sourceType is not "compound", should include compound column
        assertTrue(html.contains(">Compound<"), "Should have Compound column when sourceType is not compound")
    }

    @Test
    fun testPresentPharmacyResultsWithEmptyWorktypes() {
        val pharmacy = Pharmacy(
            uri = "https://nap.nprod.net/pharmacy/1",
            worktypes = mutableSetOf(),
            pharmacology = null,
            organism = null,
            compounds = mutableSetOf()
        )
        
        val html = createHTML().div {
            presentPharmacyResults(listOf(pharmacy), "organism")
        }
        
        // Should handle empty worktypes gracefully
        assertTrue(html.contains("pharmacy/1"), "Should contain pharmacy reference")
    }

    @Test
    fun testPresentPharmacyResultsWithEmptyCompounds() {
        val pharmacy = Pharmacy(
            uri = "https://nap.nprod.net/pharmacy/1",
            worktypes = mutableSetOf(),
            pharmacology = null,
            organism = null,
            compounds = mutableSetOf()
        )
        
        val html = createHTML().div {
            presentPharmacyResults(listOf(pharmacy), "organism")
        }
        
        // Should handle empty compounds gracefully when compounds column is shown
        assertTrue(html.contains("table"), "Should still generate table structure")
    }
}