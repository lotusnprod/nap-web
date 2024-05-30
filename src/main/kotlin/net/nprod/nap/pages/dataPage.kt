import kotlinx.html.*
import kotlinx.html.dom.createHTMLDocument
import kotlinx.html.dom.serialize

fun dataPage(title: String, content: DIV.() -> Unit): String {
   return createHTMLDocument().html {
       head {
           title { +title }
           link(
               rel = "stylesheet",
               href = "https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css",
           ) {
               integrity = "sha384-QWTKZyjpPEjISv5WaRU9OFeRpok6YctnYmDr5pNlyT2bRjXh0JMhjY6hW+ALEwIH"
               attributes["crossorigin"] = "anonymous"
           }
       }
       body {
           div(classes =  "content", block = content)
           script(src = "https://code.jquery.com/jquery-3.7.1.slim.min.js") {}
           script(src = "https://cdn.jsdelivr.net/npm/@popperjs/core@2.11.8/dist/umd/popper.min.js") {}
           script(
               src = "https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js") {
               integrity = "sha384-QWTKZyjpPEjISv5WaRU9OFeRpok6YctnYmDr5pNlyT2bRjXh0JMhjY6hW+ALEwIH"
               attributes["crossorigin"] = "anonymous"
           }
       }
   }.serialize(true)
}
