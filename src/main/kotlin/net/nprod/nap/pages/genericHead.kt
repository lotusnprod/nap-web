package net.nprod.nap.pages

import kotlinx.html.MetaDataContent
import kotlinx.html.link
import kotlinx.html.meta
import kotlinx.html.title

fun MetaDataContent.genericHead(title: String?=null, description: String? = null, block: MetaDataContent.() -> Unit = {}) {
    meta(charset = "utf-8")
    meta(name = "viewport", content = "width=device-width, initial-scale=1")
    meta(name = "description", content = description ?: "Nap: the Open database of Ethnomedical and Ethnopharmacological information" )
    meta(name = "author", content = "The LOTUS devs")
    link(rel = "icon", href = "/assets/images/favicon.ico")
    link(rel = "stylesheet", type = "text/css", href = "/assets/css/style.css")
    title { +(title?: "NAP" ) }
    link(
        rel = "stylesheet",
        // Loaded from https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css
        href = "/assets/css/bootstrap.min.css",
    ) {
        attributes["crossorigin"] = "anonymous"
    }

    block()
}