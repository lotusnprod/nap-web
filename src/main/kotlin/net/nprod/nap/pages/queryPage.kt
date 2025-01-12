import kotlinx.html.*
import kotlinx.html.dom.createHTMLDocument
import kotlinx.html.dom.serialize
import net.nprod.nap.pages.defaultFinalScripts
import net.nprod.nap.pages.genericHead
import net.nprod.nap.pages.navbar

fun queryPage(): String {
    return createHTMLDocument().html {
        head {
            genericHead(title="Snorql: A SPARQL Explorer - Extended Edition") {
                link(rel = "stylesheet", type = "text/css", href = "assets/codemirror/lib/codemirror.css")
                link(rel = "stylesheet", type = "text/css", href = "assets/codemirror/addon/display/fullscreen.css")
                link(rel = "stylesheet", type = "text/css", href = "assets/css/bootstrap-treeview.min.css")
            }
        }

        body {
            onLoad = "start()"

            navbar()

            div(classes = "snorql container-fluid") {
                div(classes = "row") {
                    div(classes = "col-md-8") {
                        div(classes = "row") {
                            div(classes = "col-md-12") {
                                h4 { +"SPARQL Query:" }
                                textArea(rows = "9") {
                                    attributes.put("name", "query")
                                    id = "querytext"
                                }
                            }
                        }

                        div(classes = "row") {
                            div(classes = "col-md-12 buttons-holder") {
                                input(classes = "btn btn-success", type = InputType.button) {
                                    value = "Query"
                                    id = "query-button"
                                }
                                input(classes = "btn btn-danger", type = InputType.button) {
                                    value = "Reset"
                                    id = "reset-button"
                                }
                                input(classes = "btn btn-warning", type = InputType.button) {
                                    value = "Export CSV"
                                    id = "export-csv"
                                }
                                input(classes = "btn btn-primary", type = InputType.button)
                                {
                                    value = "Export JSON"
                                    id = "export-json"
                                }
                                input(classes = "btn btn-info", type = InputType.button) {
                                    value = "Export XML"
                                    id = "export-xml"
                                }
                                input(classes = "btn btn-dark", type = InputType.button)
                                {
                                    value = "Fullscreen Mode"
                                    id = "enter-fullscreen"
                                }
                            }
                        }
                    }

                    div(classes = "col-md-4") {
                        h4 { +"SPARQL Examples:" }
                        div(classes = "panel panel-default") {
                            div(classes = "panel-body") {
                                id = "examplesMainBody"
                                div(
                                    classes = "list-group"
                                ) {
                                    id = "examples"
                                }
                            }
                        }
                    }
                }
            }

            div(classes = "container-fluid") {
                div(classes = "row") {
                    div(classes = "col-md-12") {
                        div(classes = "pre-scrollable") {
                            id = "result"
                            span {}
                        }
                    }
                }
            }

            footer(classes = "footer navbar-fixed-bottom") {
                id = "footer"
                div(classes = "container-fluid") {
                    p {
                        +"Powered by "
                        a(href = "#") {
                            id = "poweredby"
                            +"Snorql"
                        }
                    }
                }
            }

            div(classes = "navbar-fixed-top") {
                id = "fullscreen-navbar"
                div(classes = "container") {
                    div(classes = "row") {
                        div(classes = "col-md-12") {
                            input(classes = "btn btn-danger", type = InputType.button) {
                                value = "Exit Fullscreen"
                                id = "exit-fullscreen"
                            }
                            input(classes = "btn btn-primary", type = InputType.button) {
                                value = "Examples"
                                id = "examples-fullscreen"
                            }
                        }
                    }
                }
            }

            div(classes = "modal") {
                id = "examplesModal"
                attributes["tabIndex"] = "-1"
                attributes["role"] = "dialog"
                div(classes = "modal-dialog") {
                    attributes["role"] = "document"
                    div(classes = "modal-content") {
                        div(classes = "modal-header") {
                            h3(classes = "modal-title text-primary") { +"SPARQL Examples" }
                        }
                        div(classes = "modal-body") {
                            id = "examplesModalBody"
                            div(classes = "form-group") {
                                label(classes = "sr-only") {
                                    attributes["htmlFor"] = "input-search-fs"
                                    +"Search Tree:"
                                }
                                input(classes = "form-control") {
                                    id = "input-search-fs"
                                    placeholder = "Type part of the query file name to search for..."
                                    value = ""
                                }
                            }
                            button(classes = "btn btn-success") {
                                id = "btn-search-fs"
                                +"Search"
                            }
                            button(classes = "btn btn-default") {
                                id = "btn-clear-search-fs"
                                +"Clear"
                            }
                            hr {}
                            div(classes = "list-group") {
                                id = "examples-fs"

                            }
                        }
                        div(classes = "modal-footer") {}
                    }
                }
            }
            defaultFinalScripts()

            script(type = ScriptType.textJavaScript, src = "assets/codemirror/lib/codemirror.js") {}
            script(type = ScriptType.textJavaScript, src = "assets/codemirror/mode/javascript/javascript.js") {}
            script(type = ScriptType.textJavaScript, src = "assets/codemirror/addon/selection/active-line.js") {}
            script(type = ScriptType.textJavaScript, src = "assets/codemirror/addon/edit/matchbrackets.js") {}
            script(type = ScriptType.textJavaScript, src = "assets/codemirror/addon/display/fullscreen.js") {}
            script(type = ScriptType.textJavaScript, src = "assets/codemirror/sparql.js") {}
            script(type = ScriptType.textJavaScript, src = "assets/js/bootstrap-treeview.min.js") {}
            script(type = ScriptType.textJavaScript, src = "assets/js/sparql.js") {}
            script(type = ScriptType.textJavaScript, src = "assets/js/namespaces.js") {}
            script {
                unsafe {
                    raw(
                        """
                                            var editor = CodeMirror.fromTextArea(document.getElementById("querytext"), {
                                              lineNumbers: true,
                                              mode: "application/sparql-query",
                                              matchBrackets: true
                                            });
                                        """.trimIndent()
                    )
                }
            }
            script(type = ScriptType.textJavaScript, src = "assets/js/snorql.js") {}
            script(type = ScriptType.textJavaScript, src = "assets/js/script.js") {}
        }
    }.serialize(true)
}