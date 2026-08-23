package net.nprod.nap.pages

import net.nprod.nap.pages.query.QueryView
import net.nprod.nap.pages.query.QueryViewData
import kotlin.test.Test
import kotlin.test.assertContains

class DisclaimerTest {

    @Test
    fun testDefaultPageCarriesTheDisclaimer() {
        val html = defaultPage("Any page")

        assertContains(html, """id="site-disclaimer"""")
        assertContains(html, "It is not medical advice")
        assertContains(html, "no warranty as to their accuracy")
        assertContains(html, "go back to the cited publication")
    }

    @Test
    fun testDisclaimerIsDiscreet() {
        val html = defaultPage("Any page")

        // Muted small text in a plain footer, not an alert or a banner
        assertContains(html, "text-muted small")
        assertContains(html, "site-disclaimer border-top")
    }

    @Test
    fun testQueryPageCarriesTheDisclaimer() {
        // The SPARQL page builds its own document instead of going through defaultPage,
        // so it has to be checked separately.
        val html = QueryView.render(QueryViewData())

        assertContains(html, """id="site-disclaimer"""")
        assertContains(html, "It is not medical advice")
    }
}
