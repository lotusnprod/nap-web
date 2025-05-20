package net.nprod.nap.helpers

/**
 * Converts a remote URI to a local link that works in both development and production.
 * In development mode, it replaces the base URI with localhost.
 * In production mode, it replaces the base URI with a relative path.
 *
 * @param uri The URI to convert to a local link
 * @return A local link that works in both development and production
 */
fun localLinks(uri: String): String {
    return if (System.getenv("ENVIRONMENT") == "development") {
        uri.replace("https://nap.nprod.net", "http://localhost:8080")
    } else {
        uri.replace("https://nap.nprod.net/", "/")
    }
}
