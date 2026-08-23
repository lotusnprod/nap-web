package net.nprod.nap.types

/**
 * No entity of [entityType] exists for [identifier].
 *
 * Replaces the bare `throw Exception("No compound found for …")` calls, which
 * controllers used to swallow into a blanket `catch (e: Exception) { null }` —
 * making a backend outage indistinguishable from a missing record. StatusPages
 * turns this into an honest 404.
 */
class EntityNotFoundException(val entityType: String, val identifier: String?) :
    RuntimeException("No $entityType found for $identifier")
