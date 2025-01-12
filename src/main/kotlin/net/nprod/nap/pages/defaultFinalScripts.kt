package net.nprod.nap.pages

import kotlinx.html.BODY
import kotlinx.html.script

fun BODY.defaultFinalScripts() {
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
}