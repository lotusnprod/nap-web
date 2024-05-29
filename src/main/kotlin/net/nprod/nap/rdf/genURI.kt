import org.apache.jena.graph.Node
import org.apache.jena.rdf.model.ResourceFactory

fun genURI(type: String, id: String): String {
    if (type.contains("/") || id.contains("/"))
        throw IllegalArgumentException("Type and id should not contain /")

    return "https://nap.nprod.net/$type/$id"
}

fun genURI(obj: String): String {
    if (obj.contains("/"))
        throw IllegalArgumentException("Object should not contain /")

    return "https://nap.nprod.net/$obj"
}

fun genNode(obj: String): Node {
    return ResourceFactory.createResource(genURI(obj)).asNode()
}