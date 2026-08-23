package net.nprod.nap.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import net.nprod.nap.pages.invalidEntryPage
import net.nprod.nap.rdf.SparqlBadRequestException
import net.nprod.nap.rdf.SparqlUnavailableException
import net.nprod.nap.types.EntityNotFoundException
import org.apache.jena.query.QueryParseException

/**
 * Single place where a failure becomes a response.
 *
 * Before this existed, an unknown compound id produced a 500 with a raw Jena
 * exception, a newline in a search term produced a 500, and a missing entity
 * produced a 200 with an "Invalid id" body. Every handler here logs at a level
 * that matches whose fault it is, and none of them echo the exception to the
 * client.
 */
fun Application.configureErrorHandling() {
    install(StatusPages) {
        exception<EntityNotFoundException> { call, cause ->
            call.application.log.info("Not found: {}", cause.message)
            call.respondError(HttpStatusCode.NotFound, cause.entityType, cause.identifier)
        }
        exception<IllegalArgumentException> { call, cause ->
            // genURI rejects identifiers that cannot be spliced into an IRIREF.
            call.application.log.info("Bad request on {}: {}", call.request.path(), cause.message)
            call.respondError(HttpStatusCode.BadRequest, "request", null)
        }
        exception<QueryParseException> { call, cause ->
            call.application.log.warn("Malformed SPARQL from user input on ${call.request.path()}", cause)
            call.respondError(HttpStatusCode.BadRequest, "query", null)
        }
        exception<SparqlBadRequestException> { call, cause ->
            call.application.log.warn("SPARQL backend rejected the query on ${call.request.path()}", cause)
            call.respondError(HttpStatusCode.BadRequest, "query", null)
        }
        exception<SparqlUnavailableException> { call, cause ->
            call.application.log.error("SPARQL backend unavailable on ${call.request.path()}", cause)
            call.response.header(HttpHeaders.RetryAfter, "15")
            call.respondError(HttpStatusCode.ServiceUnavailable, "backend", null)
        }
        exception<Throwable> { call, cause ->
            call.application.log.error("Unhandled exception on ${call.request.path()}", cause)
            call.respondError(HttpStatusCode.InternalServerError, "server", null)
        }
        status(HttpStatusCode.NotFound) { call, status ->
            call.respondError(status, "page", call.request.path())
        }
    }
}

/** Content-negotiated error body. Never carries a stack trace. */
private suspend fun ApplicationCall.respondError(
    status: HttpStatusCode,
    entityType: String,
    identifier: String?
) {
    if (request.header(HttpHeaders.Accept)?.contains("application/json") == true) {
        respond(status, mapOf("error" to status.description, "type" to entityType))
    } else {
        respondText(invalidEntryPage(entityType, identifier ?: ""), ContentType.Text.Html, status)
    }
}
