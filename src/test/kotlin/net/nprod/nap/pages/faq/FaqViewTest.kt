package net.nprod.nap.pages.faq

import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals

class FaqViewTest {

    @Test
    fun testDefaultTitle() {
        assertEquals("FAQ", FaqViewData().title)
    }

    @Test
    fun testRenderUsesTitle() {
        val html = FaqView.render(FaqViewData(title = "Some FAQ"))

        assertContains(html, "<title>Some FAQ</title>")
        assertContains(html, "Frequently asked questions")
    }

    @Test
    fun testRenderExplainsOrganismVersusTaxon() {
        val html = FaqView.render(FaqViewData())

        assertContains(html, "What is the difference between an organism and a taxon?")
        assertContains(html, "Why does the search box say Taxon and not Organism?")
        assertContains(html, "specimen record")
        assertContains(html, "taxonomic hierarchy")
    }

    @Test
    fun testRenderDescribesTheEntities() {
        val html = FaqView.render(FaqViewData())

        listOf("Citation", "Organism", "Taxon", "Compound", "Pharmacy", "Pharmacology", "Worktype", "Extract")
            .forEach { entity ->
                assertContains(html, entity, message = "FAQ should describe the $entity entity")
            }
    }

    @Test
    fun testRenderLinksToTheSparqlEditor() {
        val html = FaqView.render(FaqViewData())

        assertContains(html, "/sparql")
        assertContains(html, "/raw/sparql")
    }
}
