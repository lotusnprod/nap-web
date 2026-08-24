package net.nprod.nap.rdf

import net.nprod.nap.test.InMemoryFusekiServer
import org.junit.AfterClass
import org.junit.BeforeClass
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class TaxonQueriesTest {
    companion object {
        private lateinit var fusekiServer: InMemoryFusekiServer
        private lateinit var sparqlConnector: SparqlConnector

        @JvmStatic
        @BeforeClass
        fun setupServer() {
            fusekiServer = InMemoryFusekiServer()
            val endpoint = fusekiServer.start()
            System.setProperty("SPARQL_SERVER", endpoint)
            sparqlConnector = SparqlConnector()

            fusekiServer.loadN3Data(
                """
                @prefix n: <https://nap.nprod.net/> .

                # The organism was recorded under one name and reconciled to a taxon that
                # carries another. This is the case that used to show the organism's name.
                <https://nap.nprod.net/organism/8001> a n:organism ;
                    n:number 8001 ;
                    n:familyname "RANUNCULACEAE" ;
                    n:genusname "Adonis" ;
                    n:speciesname "aleppica" ;
                    n:has_taxon <https://nap.nprod.net/taxon/8001> .

                <https://nap.nprod.net/taxon/8001> a n:taxon ;
                    n:number 8001 ;
                    n:name "Achillea aleppica" .

                <https://nap.nprod.net/taxon/8002> a n:taxon ;
                    n:number 8002 .
                """.trimIndent()
            )
        }

        @JvmStatic
        @AfterClass
        fun teardownServer() {
            fusekiServer.stop()
            System.clearProperty("SPARQL_SERVER")
        }
    }

    @Test
    fun testTaxonNameIsTheTaxonsOwnNotAnOrganisms() {
        assertEquals("Achillea aleppica", taxonName(sparqlConnector, "8001"))
    }

    @Test
    fun testTaxonNameIsNullWhenTheTaxonHasNone() {
        assertNull(taxonName(sparqlConnector, "8002"))
    }

    @Test
    fun testTaxonNameIsNullForAnUnknownTaxon() {
        assertNull(taxonName(sparqlConnector, "999999"))
    }
}
