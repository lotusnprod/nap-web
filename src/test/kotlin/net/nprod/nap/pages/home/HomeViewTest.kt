package net.nprod.nap.pages.home

import net.nprod.nap.pages.TourStep
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertFalse

class HomeViewTest {

    private val html = HomeView.render(HomeViewData())

    @Test
    fun testExplainsWhatNapIs() {
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
    fun testOffersEntryPoints() {
        assertContains(html, """href="/organism/search?query=salix"""")
        assertContains(html, """href="/compound/1"""")
        assertContains(html, """href="/pharmacology/1"""")
        assertContains(html, """href="/sparql"""")
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
