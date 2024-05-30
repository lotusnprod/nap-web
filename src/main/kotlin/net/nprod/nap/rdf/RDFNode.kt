import kotlinx.html.HtmlBlockTag
import kotlinx.html.a
import org.apache.jena.datatypes.xsd.XSDDatatype
import org.apache.jena.rdf.model.RDFNode
import org.apache.jena.rdf.model.Resource

fun HtmlBlockTag.rdfNode(it: RDFNode) {
    when (it) {
        is Resource -> {
            a(href = it.uri) { +it.uri }
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
