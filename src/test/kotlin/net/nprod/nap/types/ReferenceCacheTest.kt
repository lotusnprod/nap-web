package net.nprod.nap.types

import net.nprod.nap.rdf.SparqlUnavailableException
import net.nprod.nap.test.InMemoryFusekiServer
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * The failure modes the reference caches used to have when they ran SPARQL from
 * `<clinit>`: a cold start against a down Fuseki poisoned the class for the life
 * of the JVM (`ExceptionInInitializerError` → `NoClassDefFoundError` forever),
 * and once loaded the data could never be refreshed.
 */
class ReferenceCacheTest {

    private val worktypeUri = "https://nap.nprod.net/worktype/1"

    private lateinit var server: InMemoryFusekiServer
    private lateinit var endpoint: String
    private var originalServer: String? = null

    @BeforeTest
    fun setUp() {
        originalServer = System.getProperty("SPARQL_SERVER")
        server = InMemoryFusekiServer()
        endpoint = server.start()
        System.setProperty("SPARQL_SERVER", endpoint)
        Worktype.Cache.invalidate()
    }

    @AfterTest
    fun tearDown() {
        server.stop()
        Worktype.Cache.invalidate()
        if (originalServer != null) System.setProperty("SPARQL_SERVER", originalServer!!)
        else System.clearProperty("SPARQL_SERVER")
    }

    @Test
    fun testFailedLoadDoesNotPoisonTheCache() {
        System.setProperty("SPARQL_SERVER", "http://127.0.0.1:9/sparql")

        // First touch with the backend down: the failure surfaces to the caller…
        assertFailsWith<SparqlUnavailableException> { Worktype.Cache[worktypeUri] }

        // …and the cache is still usable once the backend comes back.
        System.setProperty("SPARQL_SERVER", endpoint)
        assertNotNull(Worktype.Cache[worktypeUri], "The cache must recover without a restart")
        assertTrue(Worktype.Cache.isLoaded())
    }

    @Test
    fun testCacheReloadsWhenTheEndpointChanges() {
        // A worktype that only this server knows about.
        server.loadN3Data(
            """
            @prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .
            @prefix n: <https://nap.nprod.net/> .

            <https://nap.nprod.net/worktype/999> rdf:type n:worktype ;
                n:code "W999" ;
                n:name "Only on the first server" .
            """.trimIndent()
        )
        assertNotNull(Worktype.Cache["https://nap.nprod.net/worktype/999"])

        val other = InMemoryFusekiServer()
        val otherEndpoint = other.start()
        try {
            System.setProperty("SPARQL_SERVER", otherEndpoint)

            // Contents must come from the new endpoint, not from the old load.
            assertNull(Worktype.Cache["https://nap.nprod.net/worktype/999"])
            assertNotNull(Worktype.Cache[worktypeUri])
        } finally {
            other.stop()
            System.setProperty("SPARQL_SERVER", endpoint)
        }
    }

    @Test
    fun testLoadsReferenceDataFromTheSeedFixture() {
        val worktype = Worktype.Cache[worktypeUri]

        assertNotNull(worktype)
        assertEquals(worktypeUri, worktype.uri)
        assertTrue(Worktype.Cache.values().isNotEmpty())
    }

    @Test
    fun testNullUriIsNeverALookup() {
        assertNull(Worktype.Cache[null])
        assertTrue(!Worktype.Cache.isLoaded(), "A null lookup must not trigger a load")
    }
}
