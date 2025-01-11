import kotlinx.html.*
import kotlinx.html.dom.createHTMLDocument
import kotlinx.html.dom.serialize

fun dataPage(title: String, content: DIV.() -> Unit): String {
   return createHTMLDocument().html {
       head {
           title { +title }
           link(
               rel = "stylesheet",
               // Loaded from https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css
               href = "/assets/css/bootstrap.min.css",
           ) {
               attributes["crossorigin"] = "anonymous"
           }
           meta(name = "viewport", content = "width=device-width, initial-scale=1")
       }
       body {
           nav(classes = "navbar navbar-expand-lg bg-body-tertiary") {
               div(classes = "container-fluid") {
                   a(classes = "navbar-brand", href = "#") { +"Nap" }

                   button(
                       type = ButtonType.button,
                       classes = "navbar-toggler"
                   ) {
                       attributes["data-bs-toggle"] = "collapse"
                       attributes["data-bs-target"] = "#navbarSupportedContent"
                       attributes["aria-controls"] = "navbarSupportedContent"
                       attributes["aria-expanded"] = "false"
                       attributes["aria-label"] = "Toggle navigation"
                       span(classes = "navbar-toggler-icon") {}
                   }

                   div(classes = "collapse navbar-collapse") {
                       id = "navbarSupportedContent"

                       ul(classes = "navbar-nav me-auto mb-2 mb-lg-0") {
                           li(classes = "nav-item") {
                               a(classes = "nav-link active", href = "/") {
                                   attributes["aria-current"] = "page"
                                   +"Home"
                               }
                           }
                           li(classes = "nav-item") {
                               a(classes = "nav-link", href = "/") { +"Sparql" }
                           }

                       }
                   }
               }
           }
           div(classes =  "content", block = content)
           // Loaded from https://code.jquery.com/jquery-3.7.1.slim.min.js
           script(src = "/assets/js/jquery-3.7.1.slim.min.js") {}
           // Loaded from https://cdn.jsdelivr.net/npm/@popperjs/core@2.11.8/dist/umd/popper.min.js
           script(src = "/assets/js/popper.min.js") {}
           script(
               // Loaded from https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js
               src = "/assets/js/bootstrap.bundle.min.js") {
               attributes["crossorigin"] = "anonymous"
           }
       }
   }.serialize(true)
}
