package net.nprod.nap.pages.contact

import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertFalse

class ContactViewTest {

    private val html = ContactView.render(ContactViewData())

    @Test
    fun testPageRendersTheContactHeading() {
        assertContains(html, "<title>Contact</title>")
        assertContains(html, "Write to us")
    }

    @Test
    fun testAddressIsNotInTheMarkup() {
        // The whole point of the page: nothing a scraper can pick up without running the script
        assertFalse(html.contains("research@bjonnh.net"), "The plain address leaked into the HTML")
        assertFalse(html.contains("mailto:research"), "A plain mailto: leaked into the HTML")
    }

    @Test
    fun testObfuscatedAddressDecodesToTheRealOne() {
        assertContains(html, """data-a="gra.uaabwo@upenrfre"""")

        val decoded = "gra.uaabwo@upenrfre".reversed().map { c ->
            when {
                c in 'a'..'z' -> 'a' + (c - 'a' + 13) % 26
                c in 'A'..'Z' -> 'A' + (c - 'A' + 13) % 26
                else -> c
            }
        }.joinToString("")

        kotlin.test.assertEquals("research@bjonnh.net", decoded)
    }

    @Test
    fun testNoScriptFallbackSpellsTheAddressOut() {
        assertContains(html, "research (at) bjonnh (dot) net")
    }
}
