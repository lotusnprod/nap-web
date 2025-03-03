import as_local_link_if_dev
import kotlinx.html.HtmlBlockTag
import kotlinx.html.a
import org.apache.jena.datatypes.xsd.XSDDatatype
import org.apache.jena.rdf.model.RDFNode
import org.apache.jena.rdf.model.Resource

fun HtmlBlockTag.rdfNode(it: RDFNode) {
    when (it) {
        is Resource -> {
            val localUri = it.uri.as_local_link_if_dev
            a(href = localUri) { +localUri }
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
