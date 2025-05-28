package net.nprod.nap.pages

import kotlinx.html.*
import net.nprod.nap.rdf.SparqlConnector

fun BODY.defaultFinalScripts() {
    // Add SPARQL queries display section
    val queries = SparqlConnector.getTrackedQueries()
    if (queries.isNotEmpty()) {
        div(classes = "container mt-5") {
            div(classes = "row") {
                div(classes = "col-12") {
                    div(classes = "card") {
                        div(classes = "card-header bg-secondary text-white") {
                            h5(classes = "mb-0") {
                                a(href = "#", classes = "text-white text-decoration-none") {
                                    id = "toggle-queries"
                                    +"Show SPARQL Queries (${queries.size})"
                                }
                            }
                        }
                        div(classes = "card-body", block = {
                            id = "sparql-queries"
                            style = "display: none;"
                            
                            queries.forEachIndexed { index, query ->
                                div(classes = "mb-3") {
                                    div(classes = "d-flex justify-content-between align-items-start mb-2") {
                                        h6 { +"Query ${index + 1}" }
                                        button(classes = "btn btn-sm btn-outline-primary") {
                                            attributes["data-query-index"] = index.toString()
                                            attributes["onclick"] = "copyQuery(${index})"
                                            +"Copy"
                                        }
                                    }
                                    pre(classes = "bg-light p-3 rounded") {
                                        code {
                                            id = "query-${index}"
                                            +query
                                        }
                                    }
                                }
                            }
                        })
                    }
                }
            }
        }
    }
    // Loaded from https://code.jquery.com/jquery-3.7.1.min.js
    script(src = "/assets/js/jquery.min.js") {}
    // Loaded from https://cdn.jsdelivr.net/npm/@popperjs/core@2.11.8/dist/umd/popper.min.js
    script(src = "/assets/js/popper.min.js") {}
    script(
        // Loaded from https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js
        src = "/assets/js/bootstrap.bundle.min.js"
    ) {
        attributes["crossorigin"] = "anonymous"
    }

    // Custom scripts for the application
    script(src = "/assets/js/script.js") {}
    
    // Add JavaScript for SPARQL queries functionality
    if (queries.isNotEmpty()) {
        script {
            unsafe {
                +"""
                    // Toggle SPARQL queries display
                    document.getElementById('toggle-queries').addEventListener('click', function(e) {
                        e.preventDefault();
                        var queriesDiv = document.getElementById('sparql-queries');
                        var toggleText = this;
                        
                        if (queriesDiv.style.display === 'none') {
                            queriesDiv.style.display = 'block';
                            toggleText.textContent = 'Hide SPARQL Queries (${queries.size})';
                        } else {
                            queriesDiv.style.display = 'none';
                            toggleText.textContent = 'Show SPARQL Queries (${queries.size})';
                        }
                    });
                    
                    // Copy query to clipboard
                    function copyQuery(index) {
                        var queryElement = document.getElementById('query-' + index);
                        var queryText = queryElement.textContent;
                        
                        // Create a temporary textarea to copy the text
                        var textarea = document.createElement('textarea');
                        textarea.value = queryText;
                        textarea.style.position = 'fixed';
                        textarea.style.opacity = '0';
                        document.body.appendChild(textarea);
                        textarea.select();
                        
                        try {
                            document.execCommand('copy');
                            // Change button text temporarily
                            var button = document.querySelector('[data-query-index="' + index + '"]');
                            var originalText = button.textContent;
                            button.textContent = 'Copied!';
                            button.classList.remove('btn-outline-primary');
                            button.classList.add('btn-success');
                            
                            setTimeout(function() {
                                button.textContent = originalText;
                                button.classList.remove('btn-success');
                                button.classList.add('btn-outline-primary');
                            }, 2000);
                        } catch (err) {
                            console.error('Failed to copy query:', err);
                        }
                        
                        document.body.removeChild(textarea);
                    }
                """.trimIndent()
            }
        }
    }
}
