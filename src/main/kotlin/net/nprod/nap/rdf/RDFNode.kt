import kotlinx.html.HtmlBlockTag
import kotlinx.html.a
import org.apache.jena.datatypes.xsd.XSDDatatype
import org.apache.jena.rdf.model.RDFNode
import org.apache.jena.rdf.model.Resource

fun HtmlBlockTag.rdfNode(it: RDFNode) {
    when (it) {
        is Resource -> {
            if (System.getenv("ENVIRONMENT") == "development") {
                val uri = it.uri.replace("https://nap.nprod.net", "http://localhost:8080")
                a(href = uri) { +uri }
            } else {
                a(href = it.uri) { +it.uri }
            }
        }

        else -> {
            when (it.asLiteral().datatype) {
                XSDDatatype.XSDinteger -> {
                    +it.asLiteral().int.toString()
                }
                XSDDatatype.XSDfloat -> {
                    +it.asLiteral().float.toString()
                }
                else -> {
                    +it.toString()
                }
            }
        }
    }
}
