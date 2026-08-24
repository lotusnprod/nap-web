package net.nprod.nap.pages.home

import net.nprod.nap.pages.TourStep
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class HomeViewTest {

    private val html = HomeView.render(HomeViewData())

    @Test
    fun testExplainsWhatNapIs() {
        assertContains(html, "Natural Actives &amp; Pharmacology")
        assertContains(html, "ethnomedical and ethnopharmacological")
        assertContains(html, "How it works")
    }

    @Test
    fun testExplainsTheDataModelInPassing() {
        // The home page has to make the organism/taxon distinction visible, it is the thing
        // people trip on. The detail lives in the FAQ.
        listOf("citation", "organism", "experiments", "taxon").forEach { term ->
            assertContains(html, "<strong>$term</strong>", message = "the home page should call out $term")
        }
        assertContains(html, """href="/faq"""")
    }

    @Test
    fun testEveryEntryPointLeadsToItsOwnSearch() {
        // The point people missed: the cards used to only offer a pre-run example, so the
        // only way to search for something else was the box in the navigation bar
        // The activity card opens the list of them rather than an empty search box: that
        // vocabulary is worth reading through, and it filters as you type.
        listOf("/organism/search", "/compound/search", "/pharmacology", "/sparql").forEach { path ->
            // The button of the card, not the same link in the navigation bar
            assertContains(
                html,
                """<a class="btn btn-sm btn-primary" href="$path">""",
                message = "the cards should open $path"
            )
        }
    }

    @Test
    fun testEveryEntryPointAlsoShowsAnExample() {
        assertContains(html, """href="/organism/search?query=salix"""")
        assertContains(html, """href="/compound/1"""")
        assertContains(html, """href="/pharmacology/1"""")
        assertContains(html, """href="/sparql?q=""")

        assertEquals(4, Regex("See an example").findAll(html).count(), "one example per card, no more")
    }

    @Test
    fun testPointsAtTheTutorialWithoutTurningItOn() {
        assertContains(html, "Turn on")
        assertContains(html, """data-tour-step="${TourStep.HOME_INTRO}"""")
    }

    @Test
    fun testTheIntroStepBelongsToThisPageOnly() {
        // Not shared: it would otherwise be offered on pages that have no such card
        val intro = html.substringAfter("""data-tour-step="${TourStep.HOME_INTRO}"""").substringBefore(">")

        assertFalse(intro.contains("data-tour-shared"), "the home intro is specific to the home page")
    }
}
