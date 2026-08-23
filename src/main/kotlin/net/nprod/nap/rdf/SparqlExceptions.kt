package net.nprod.nap.rdf

/**
 * The SPARQL backend could not be reached, timed out, or the circuit breaker is open.
 *
 * This is a server-side (503) condition: the request was fine, the backend was not.
 */
class SparqlUnavailableException(message: String, cause: Throwable? = null) : RuntimeException(message, cause)

/**
 * The SPARQL backend rejected the query with a 4xx.
 *
 * This is a client-side (400) condition: retrying will not help, and the query
 * text usually originates from user input.
 */
class SparqlBadRequestException(message: String, cause: Throwable? = null) : RuntimeException(message, cause)
