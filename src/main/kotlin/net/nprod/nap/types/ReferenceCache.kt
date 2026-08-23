package net.nprod.nap.types

import net.nprod.nap.rdf.SparqlConnector
import org.slf4j.LoggerFactory
import java.time.Duration
import java.time.Instant

/**
 * Lazily loaded, TTL-refreshed cache of a small reference table (worktypes,
 * genders, dose units, …).
 *
 * These used to be `object` initialisers that ran SPARQL from `<clinit>`. When
 * Fuseki was slow or down at first touch the initialiser threw, the JVM marked
 * the class erroneous, and *every* later access threw `NoClassDefFoundError` for
 * the life of the process. This class replaces that with:
 *
 * - **lazy** loading, on first use rather than at class load;
 * - **retryable** failures — a failed load leaves the cache empty, not poisoned;
 * - **stale-tolerant** reads — once loaded, a later refresh failure serves the
 *   previous contents instead of failing the request;
 * - a **TTL**, so reference data can change without a redeploy;
 * - an **endpoint check**, so a cache loaded against one SPARQL server is
 *   discarded when `SPARQL_SERVER` changes (tests switch endpoints per class).
 */
abstract class ReferenceCache<T : Any>(private val ttl: Duration = DEFAULT_TTL) {

    init {
        @Suppress("LeakingThis")
        register(this)
    }

    /** Run the query and return the uri → entity map. Called under a lock. */
    protected abstract fun load(sparqlConnector: SparqlConnector): Map<String, T>

    @Volatile
    private var entries: Map<String, T> = emptyMap()

    @Volatile
    private var loadedAt: Instant? = null

    @Volatile
    private var loadedFrom: String? = null

    @Volatile
    private var nextAttemptAt: Instant = Instant.EPOCH

    private val lock = Any()

    operator fun get(uri: String?): T? {
        if (uri == null) return null
        refreshIfStale()
        return entries[uri]
    }

    /** All cached entities, loading them if needed. */
    fun values(): Collection<T> {
        refreshIfStale()
        return entries.values
    }

    /** True once a load has succeeded; consumed by readiness checks and tests. */
    fun isLoaded(): Boolean = loadedAt != null

    /** Drop the contents so the next read reloads. */
    fun invalidate() {
        synchronized(lock) {
            entries = emptyMap()
            loadedAt = null
            loadedFrom = null
            nextAttemptAt = Instant.EPOCH
        }
    }

    private fun isFresh(): Boolean {
        val at = loadedAt ?: return false
        if (loadedFrom != SparqlConnector.configuredEndpoint()) return false
        return Duration.between(at, Instant.now()) < ttl
    }

    private fun refreshIfStale() {
        if (isFresh()) return
        // Never loaded: always try, the connector's circuit breaker keeps that cheap
        // when the backend is down. Loaded but stale: back off between attempts so a
        // Fuseki outage does not turn every page view into a failed reload.
        if (loadedAt != null && Instant.now() < nextAttemptAt) return

        synchronized(lock) {
            if (isFresh()) return
            if (loadedAt != null && Instant.now() < nextAttemptAt) return

            val endpoint = SparqlConnector.configuredEndpoint()
            try {
                entries = load(SparqlConnector())
                loadedAt = Instant.now()
                loadedFrom = endpoint
                nextAttemptAt = Instant.EPOCH
                LOGGER.debug("Loaded {} entries into {}", entries.size, javaClass.name)
            } catch (e: Exception) {
                nextAttemptAt = Instant.now().plus(FAILURE_BACKOFF)
                if (loadedAt == null) {
                    // Nothing to serve — let the caller (and StatusPages) see it.
                    LOGGER.warn("Reference cache {} failed to load", javaClass.name, e)
                    throw e
                }
                LOGGER.warn(
                    "Reference cache {} refresh failed, serving {} stale entries",
                    javaClass.name, entries.size, e
                )
            }
        }
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(ReferenceCache::class.java)

        private val DEFAULT_TTL: Duration = Duration.ofMinutes(30)
        private val FAILURE_BACKOFF: Duration = Duration.ofSeconds(30)

        private val instances = mutableListOf<ReferenceCache<*>>()

        private fun register(cache: ReferenceCache<*>) {
            synchronized(instances) { instances.add(cache) }
        }

        /** Invalidate every cache that has been touched so far. */
        fun invalidateAll() {
            synchronized(instances) { instances.toList() }.forEach { it.invalidate() }
        }
    }
}
