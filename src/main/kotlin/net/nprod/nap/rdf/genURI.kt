import org.apache.jena.graph.Node
import org.apache.jena.rdf.model.ResourceFactory

/**
 * Characters allowed in a URI component that gets spliced into a SPARQL IRIREF (`<...>`).
 *
 * This is a whitelist on purpose: an IRIREF is terminated by `>`, so any component containing
 * `>`, `{`, `}`, quotes, whitespace or control characters can close the IRI and append arbitrary
 * SPARQL to the generated query. Entity types and identifiers only ever need this charset.
 */
private val SAFE_URI_COMPONENT = Regex("^[A-Za-z0-9_.~-]+$")

private fun requireSafeComponent(name: String, value: String): String {
    if (!SAFE_URI_COMPONENT.matches(value))
        throw IllegalArgumentException("$name contains characters that are not allowed in a URI component")
    return value
}

fun genURI(type: String, id: String): String {
    requireSafeComponent("Type", type)
    requireSafeComponent("Id", id)

    return "https://nap.nprod.net/$type/$id"
}

fun genURI(obj: String): String {
    requireSafeComponent("Object", obj)

    return "https://nap.nprod.net/$obj"
}

fun genNode(obj: String): Node {
    return ResourceFactory.createResource(genURI(obj)).asNode()
}
