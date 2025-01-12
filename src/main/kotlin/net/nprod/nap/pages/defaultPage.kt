import kotlinx.html.*
import kotlinx.html.dom.createHTMLDocument
import kotlinx.html.dom.serialize
import net.nprod.nap.pages.defaultFinalScripts
import net.nprod.nap.pages.genericHead
import net.nprod.nap.pages.navbar

fun defaultPage(title: String?=null, content: DIV.() -> Unit = {}): String {
   return createHTMLDocument().html {
       head {
           genericHead(title)
      }
       body {
           navbar()
           div(classes =  "content", block = content)
           defaultFinalScripts()
       }
   }.serialize(true)
}

