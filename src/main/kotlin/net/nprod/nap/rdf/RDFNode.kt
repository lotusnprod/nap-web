import kotlinx.html.HTML
import kotlinx.html.HtmlBlockTag
import kotlinx.html.a
import org.apache.jena.rdf.model.RDFNode
import org.apache.jena.rdf.model.Resource

fun HtmlBlockTag.rdfNode(it: RDFNode) {
    when (it) {
        is Resource -> {
            a(href = it.uri) { +it.uri }
        }

        else -> {
            +it.toString()
        }
    }
}
