package net.nprod.nap.pages.contact

import kotlinx.html.*
import net.nprod.nap.pages.defaultPage

/**
 * View for the contact page
 *
 * Static content. The address is not written in the markup: it is stored rot13'd and reversed, and
 * only reassembled in the browser when the visitor asks for it. That does not stop a determined
 * scraper, but it does stop the ones that just read the HTML looking for a mailto: or an @.
 */
object ContactView {

    /**
     * The contact address, rot13'd then reversed.
     *
     * Decoded by [revealScript] in the browser. Keep [obfuscatedAddress] and the plain address in
     * sync: research@bjonnh.net -> rot13 -> erfrnepu@owbaau.arg -> reversed -> the value below.
     */
    private const val OBFUSCATED_ADDRESS = "gra.uaabwo@upenrfre"

    /**
     * The same address written out for people reading without JavaScript
     */
    private const val SPELLED_OUT_ADDRESS = "research (at) bjonnh (dot) net"

    /**
     * Render the contact page
     *
     * @param data The view data for the contact page
     * @return HTML string of the contact page
     */
    fun render(data: ContactViewData): String {
        return defaultPage(data.title) {
            div("container") {
                div("row") {
                    div("col-12") {
                        h1(classes = "mt-4 mb-4") { +"Contact" }
                    }
                }

                div("row") {
                    div("col-12 col-lg-8") {
                        div("card mb-4") {
                            div("card-header bg-success text-white") {
                                h2(classes = "card-title h4 mb-0") { +"Write to us" }
                            }
                            div("card-body") {
                                p {
                                    +"Questions about the data, corrections to a record, or an idea of what to do "
                                    +"with it: the address below reaches the people who maintain NAP."
                                }

                                p(classes = "mb-3") {
                                    id = "contact-address"
                                    button(classes = "btn btn-outline-success", type = ButtonType.button) {
                                        id = "contact-reveal"
                                        attributes["data-a"] = OBFUSCATED_ADDRESS
                                        +"Show the contact address"
                                    }
                                }

                                noScript {
                                    p(classes = "mb-3") {
                                        +"With JavaScript off, the address is: "
                                        strong { +SPELLED_OUT_ADDRESS }
                                    }
                                }

                                p(classes = "text-muted small mb-0") {
                                    +"Please say which page or record number you are writing about, and include the "
                                    +"citation if your question is about where a piece of data comes from. It saves "
                                    +"a round trip."
                                }
                            }
                        }

                        div("card mb-4") {
                            div("card-header bg-secondary text-white") {
                                h2(classes = "card-title h4 mb-0") { +"Before writing" }
                            }
                            div("card-body") {
                                p {
                                    +"Most questions about what the records mean, how organisms and taxa differ, or "
                                    +"how to get the data out are already answered in the "
                                    a(href = "/faq") { +"FAQ" }
                                    +"."
                                }
                                p(classes = "mb-0") {
                                    +"If you need something the pages do not show, the "
                                    a(href = "/sparql") { +"SPARQL editor" }
                                    +" queries the same data directly and exports as CSV, JSON or XML."
                                }
                            }
                        }
                    }
                }

                revealScript()
            }
        }
    }

    /**
     * The script that turns the obfuscated value back into a usable address
     */
    private fun FlowContent.revealScript() {
        script {
            unsafe {
                +"""
                    (function () {
                        var button = document.getElementById('contact-reveal');
                        if (!button) { return; }
                        button.addEventListener('click', function () {
                            var encoded = button.getAttribute('data-a');
                            var address = encoded.split('').reverse().join('').replace(
                                /[a-zA-Z]/g,
                                function (c) {
                                    // No '&lt;' or '&amp;' in here: the page is built as XML
                                    var base = c >= 'a' ? 97 : 65;
                                    return String.fromCharCode(
                                        (c.charCodeAt(0) - base + 13) % 26 + base
                                    );
                                }
                            );
                            var link = document.createElement('a');
                            link.href = 'mailto:' + address + '?subject=' +
                                encodeURIComponent('[NAP] ');
                            link.textContent = address;
                            var holder = document.getElementById('contact-address');
                            holder.textContent = '';
                            holder.appendChild(link);
                        });
                    })();
                """.trimIndent()
            }
        }
    }
}
