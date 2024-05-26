import kotlinx.html.*
import kotlinx.html.dom.createHTMLDocument
import kotlinx.html.dom.serialize
import org.w3c.dom.Document

fun dataPage(title: String, content: DIV.() -> Unit): String {
   return createHTMLDocument().html {
       head {
           title { +title }
       }
       body {
           div(classes =  "content", block = content)
       }
   }.serialize(true)
}
