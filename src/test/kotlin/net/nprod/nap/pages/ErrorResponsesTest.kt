package net.nprod.nap.pages

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import net.nprod.nap.test.withInMemoryFuseki
import net.nprod.nap.test.withTestApplication
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * The four confirmed bad responses from the audit (plan 04, E1–E4), pinned down.
 *
 * Before StatusPages existed these were: 500 for an unknown compound, 500 for a
 * malformed search term, and 200 for a missing pharmacy.
 */
class ErrorResponsesTest {

    @Test
    fun testUnknownCompoundIsNotFound() = withInMemoryFuseki {
        val response = client.get("/compound/999999")

        assertEquals(HttpStatusCode.NotFound, response.status)
        assertFalse(response.bodyAsText().contains("Exception"), "The page must not leak a stack trace")
    }

    @Test
    fun testUnknownPharmacyIsNotFound() = withInMemoryFuseki {
        val response = client.get("/pharmacy/999999")

        assertEquals(HttpStatusCode.NotFound, response.status)
    }

    @Test
    fun testUnknownCompoundAsJsonIsNotFound() = withInMemoryFuseki {
        val response = client.get("/compound/999999") {
            header(HttpHeaders.Accept, "application/json")
        }

        assertEquals(HttpStatusCode.NotFound, response.status)
        assertTrue(response.bodyAsText().contains("error"))
    }

    @Test
    fun testMalformedSearchTermIsBadRequest() = withInMemoryFuseki {
        // A newline breaks out of the SPARQL string literal the search query builds.
        val response = client.get("/compound/search?query=a%0Ab")

        assertEquals(HttpStatusCode.BadRequest, response.status)
        assertFalse(response.bodyAsText().contains("QueryParseException"), "The page must not leak the parser error")
    }

    @Test
    fun testUnreachableBackendIsServiceUnavailable() {
        val originalServer = System.getProperty("SPARQL_SERVER")
        try {
            // Port 9 (discard) refuses or blackholes, so this exercises the real
            // connect-failure path rather than a 404 from a live server.
            withTestApplication("http://127.0.0.1:9/sparql") {
                val response = client.get("/compound/1")

                assertEquals(HttpStatusCode.ServiceUnavailable, response.status)
                assertEquals("15", response.headers[HttpHeaders.RetryAfter])
            }
        } finally {
            if (originalServer != null) System.setProperty("SPARQL_SERVER", originalServer)
        }
    }
}
