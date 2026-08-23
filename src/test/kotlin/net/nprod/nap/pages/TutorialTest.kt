package net.nprod.nap.pages

import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class TutorialTest {

    private val page = defaultPage("Any page")

    @Test
    fun testToggleIsInTheNavbar() {
        assertContains(page, """id="tutorial-toggle"""")
        // The label is filled in by the engine, which is the only side that knows the state
        assertContains(page, """aria-pressed="false"""")
    }

    @Test
    fun testEngineIsLoaded() {
        assertContains(page, "/assets/js/tutorial.js")
    }

    @Test
    fun testNothingStartsTheTourFromTheServer() {
        // Opting in happens in the browser. No page may ship a marker that turns it on.
        assertTrue(!page.contains("tour-card"), "the tour card is built client side, never rendered")
        assertTrue(!page.contains("tour-target"), "highlighting is applied client side")
    }

    @Test
    fun testNavbarDeclaresItsSteps() {
        listOf(TourStep.SEARCH_TYPE, TourStep.SEARCH_INPUT, TourStep.SEARCH_SUBMIT, TourStep.SPARQL, TourStep.FAQ)
            .forEach { step ->
                assertContains(page, """data-tour-step="$step"""", message = "navbar should declare step $step")
            }
    }

    @Test
    fun testEveryStepHasATitleAndABody() {
        val steps = Regex("""data-tour-step="(\d+)"""").findAll(page).count()
        val titles = Regex("""data-tour-title="[^"]+"""").findAll(page).count()
        val bodies = Regex("""data-tour-body="[^"]+"""").findAll(page).count()

        assertEquals(steps, titles, "every step needs a title")
        assertEquals(steps, bodies, "every step needs a body")
    }

    @Test
    fun testEveryStepOfTheChromeIsMarkedShared() {
        // This page has no content of its own, so every step in it comes from the navbar and
        // is present on every page. Such a step has to be shared or it nags everywhere.
        val steps = Regex("""data-tour-step="\d+"""").findAll(page).count()
        val shared = Regex("""data-tour-shared="true"""").findAll(page).count()

        assertEquals(steps, shared, "a step present on every page must be declared shared")
    }

    @Test
    fun testStepNumbersAreUnique() {
        val numbers = Regex("""data-tour-step="(\d+)"""").findAll(page).map { it.groupValues[1] }.toList()

        assertEquals(numbers.size, numbers.distinct().size, "two steps sharing a number order arbitrarily")
    }
}
