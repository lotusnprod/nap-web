import org.apache.jena.vocabulary.RDF

fun String.getRef(): String {
    // Gross
    if (startsWith(RDF.uri)) return split(RDF.uri).last()
    return split("https://nap.nprod.net/").last()
}
