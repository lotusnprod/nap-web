package net.nprod.nap.plugins

import compoundPage
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import net.nprod.nap.pages.naiveDataPage
import queryPage
import java.io.File

fun Application.configureRouting() {
    routing {
        get("/compound/{id}") {
            call.respondText(compoundPage(call.parameters["id"]), ContentType.Text.Html)
        }

        get("/{type}/{id}") {
            call.respondText(naiveDataPage(call.parameters["type"] ?: "unknown", call.parameters["id"]), ContentType.Text.Html)
        }
        get("/") {
            call.respondText(queryPage(), ContentType.Text.Html)
        }
        staticFiles("/assets", File("assets"))
    }
}
