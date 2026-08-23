package net.nprod.nap.rdf

import io.github.resilience4j.circuitbreaker.CircuitBreaker
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry
import io.github.resilience4j.core.IntervalFunction
import io.github.resilience4j.retry.Retry
import io.github.resilience4j.retry.RetryConfig
import org.apache.jena.atlas.web.HttpException
import org.apache.jena.query.*
import org.apache.jena.rdf.model.RDFNode
import org.apache.jena.rdf.model.Resource
import org.apache.jena.sparql.engine.http.QueryExceptionHTTP
import org.apache.jena.system.Txn
import org.slf4j.LoggerFactory
import java.net.http.HttpClient
import java.time.Duration
import java.util.concurrent.Callable
import java.util.concurrent.TimeUnit


class SparqlConnector {
    // get SPARQL_SERVER from system property first, then environment variable.
    // Missing configuration is a deployment error, so fail at construction rather
    // than producing an NPE somewhere deep in Jena at request time.
    val SPARQL_SERVER: String = configuredEndpoint()
        ?: error("SPARQL_SERVER is not configured (set the system property or the environment variable)")

    companion object {
        private val LOGGER = LoggerFactory.getLogger(SparqlConnector::class.java)

        /** How long to wait for the TCP connection to Fuseki. */
        private val CONNECT_TIMEOUT: Duration = Duration.ofSeconds(3)

        /** Ceiling on a single query, enforced both by Jena and by the HTTP layer. */
        internal const val QUERY_TIMEOUT_MS = 15_000L

        /**
         * The configured endpoint, or null when neither the system property nor the
         * environment variable is set. Reference caches use this to notice that the
         * endpoint changed under them (which happens in tests).
         */
        fun configuredEndpoint(): String? =
            System.getProperty("SPARQL_SERVER") ?: System.getenv("SPARQL_SERVER")

        /**
         * One shared, pooled JDK client with an explicit connect timeout. Jena's
         * default client has neither a connect nor a request timeout, so a wedged
         * or blackholed Fuseki would pin the request thread forever.
         */
        private val httpClient: HttpClient = HttpClient.newBuilder()
            .connectTimeout(CONNECT_TIMEOUT)
            .followRedirects(HttpClient.Redirect.NORMAL)
            .build()

        private val breakerConfig: CircuitBreakerConfig = CircuitBreakerConfig.custom()
            .slidingWindowSize(30)
            .failureRateThreshold(50f)
            .slowCallDurationThreshold(Duration.ofSeconds(10))
            .slowCallRateThreshold(50f)
            .waitDurationInOpenState(Duration.ofSeconds(15))
            .permittedNumberOfCallsInHalfOpenState(3)
            // A rejected query is the caller's fault, not the backend's: it must
            // not count towards opening the breaker.
            .ignoreException { isClientError(it) || it is QueryParseException }
            .build()

        // One breaker per endpoint. A deployment has a single SPARQL_SERVER, but the
        // state must not be shared across endpoints: one unreachable server would
        // otherwise trip the breaker for a healthy one.
        private val breakers: CircuitBreakerRegistry = CircuitBreakerRegistry.of(breakerConfig)

        private val retry: Retry = Retry.of(
            "fuseki",
            RetryConfig.custom<Any>()
                .maxAttempts(3)
                .intervalFunction(IntervalFunction.ofExponentialRandomBackoff(200L, 2.0, 0.5))
                // Only retry transport-level failures; never retry a rejected query.
                .retryOnException { !isClientError(it) && it !is QueryParseException }
                .build()
        )

        private fun breakerFor(endpoint: String): CircuitBreaker = breakers.circuitBreaker(endpoint)

        /** Exposed so readiness probes can report why the backend looks unhealthy. */
        fun breakerState(endpoint: String? = configuredEndpoint()): CircuitBreaker.State? =
            endpoint?.let { breakerFor(it).state }

        /** True for a 4xx that is not worth retrying (408 and 429 are). */
        private fun isClientError(e: Throwable): Boolean {
            val status = when (e) {
                is QueryExceptionHTTP -> e.statusCode
                is HttpException -> e.statusCode
                else -> return false
            }
            return status in 400..499 && status != 408 && status != 429
        }

        private fun <T> guarded(endpoint: String, name: String, block: () -> T): T {
            val callable = Retry.decorateCallable(retry, Callable { block() })
            try {
                return breakerFor(endpoint).executeCallable(callable)
            } catch (e: QueryParseException) {
                throw e
            } catch (e: Exception) {
                if (isClientError(e)) {
                    LOGGER.info("SPARQL {} rejected by the backend: {}", name, e.toString())
                    throw SparqlBadRequestException("SPARQL $name rejected by the backend", e)
                }
                LOGGER.warn("SPARQL {} failed (breaker={}): {}", name, breakerState(endpoint), e.toString())
                throw SparqlUnavailableException("SPARQL $name failed", e)
            }
        }

        // Data class to store query info
        data class QueryInfo(
            val query: String,
            val executionTimeMs: Long
        )

        // Thread-local storage for tracking queries
        private val queriesThreadLocal = ThreadLocal<MutableList<QueryInfo>>()

        /**
         * Start tracking SPARQL queries for the current thread
         */
        fun startTracking() {
            queriesThreadLocal.set(mutableListOf())
        }

        /**
         * Get all tracked queries for the current thread
         */
        fun getTrackedQueries(): List<QueryInfo> {
            return queriesThreadLocal.get()?.toList() ?: emptyList()
        }

        /**
         * Clear tracked queries for the current thread
         */
        fun clearTracking() {
            queriesThreadLocal.remove()
        }

        /**
         * Track a query if tracking is enabled and logging is requested.
         *
         * Visible within the module (rather than private) so it can be exercised
         * directly in unit tests without brittle reflection.
         */
        internal fun trackQuery(query: String, executionTimeMs: Long, logQuery: Boolean = true) {
            if (!logQuery) {
                return
            }

            queriesThreadLocal.get()?.add(QueryInfo(query, executionTimeMs))
        }
    }

    /**
     * Run a SELECT and materialise the results.
     *
     * @throws QueryParseException if the query text is malformed (usually user input)
     * @throws SparqlBadRequestException if the backend rejected the query
     * @throws SparqlUnavailableException if the backend is unreachable, timing out, or the breaker is open
     */
    fun getResultsOfQuery(query: String, logQuery: Boolean = true): ResultSet? {
        // Parse locally so a malformed query fails as a QueryParseException here
        // instead of costing a round trip and coming back as an opaque HTTP 400.
        val parsedQuery = QueryFactory.create(query, Syntax.syntaxARQ)
        val startTime = System.currentTimeMillis()

        try {
            return guarded(SPARQL_SERVER, "SELECT") {
                LOGGER.debug("Query: {}", query)
                newExecution(parsedQuery).use { qExec ->
                    ResultSetFactory.copyResults(qExec.execSelect())
                }
            }
        } finally {
            trackQuery(query, System.currentTimeMillis() - startTime, logQuery)
        }
    }

    /**
     * Run a CONSTRUCT into a temporary in-memory dataset and hand it to [block].
     *
     * The dataset never escapes this call, so it is always closed — the previous
     * `constructQueryIntoAQueriableDataset` leaked one TDB2 dataset (and one
     * QueryExecution) per search request.
     */
    fun <T> withConstructedDataset(query: String, logQuery: Boolean = true, block: (Dataset) -> T): T {
        val parsedQuery = QueryFactory.create(query, Syntax.syntaxARQ)
        val startTime = System.currentTimeMillis()

        val dataset = try {
            guarded(SPARQL_SERVER, "CONSTRUCT") {
                // Built inside the guarded block so a retried attempt starts from an
                // empty dataset rather than accumulating a partial result.
                val ds = DatasetFactory.createTxnMem()
                try {
                    newExecution(parsedQuery).use { qExec ->
                        Txn.executeWrite(ds) { qExec.execConstructDataset(ds) }
                    }
                    ds
                } catch (e: Throwable) {
                    ds.close()
                    throw e
                }
            }
        } finally {
            trackQuery(query, System.currentTimeMillis() - startTime, logQuery)
        }

        try {
            return Txn.calculateRead(dataset) { block(dataset) }
        } finally {
            dataset.close()
        }
    }

    private fun newExecution(query: Query): QueryExecution =
        QueryExecution.service(SPARQL_SERVER)
            .httpClient(httpClient)
            .query(query)
            .timeout(QUERY_TIMEOUT_MS, TimeUnit.MILLISECONDS)
            .build()

    fun predicateAndObjectsOf(subject: String): Map<Resource, List<RDFNode>> {
        val query = """
            SELECT ?predicate ?node
            WHERE {
                <$subject> ?predicate ?node
            }
        """.trimIndent()

        return predicateAndNodeToMap(query, logQuery = false)
    }

    fun subjectAndPredicatesOf(obj: String): Map<Resource, List<RDFNode>> {
        val query = """
            SELECT ?predicate ?node
            WHERE {
                ?node ?predicate <$obj>
            }
        """.trimIndent()

        return predicateAndNodeToMap(query, logQuery = false)
    }

    /**
     * The query should have two results ?predicate ?node
     */
    fun predicateAndNodeToMap(
        query: String,
        logQuery: Boolean = true
    ): Map<Resource, MutableList<RDFNode>> {
        val output = mutableMapOf<Resource, MutableList<RDFNode>>()
        getResultsOfQuery(query, logQuery)?.let { rs ->
            while (rs.hasNext()) {
                val qs = rs.next()
                if (output.containsKey(qs.getResource("predicate"))) {
                    output[qs.getResource("predicate")]!!.add(qs.get("node"))
                } else {
                    output[qs.getResource("predicate")] = mutableListOf(qs.get("node"))
                }
            }
        }
        return output
    }
}
