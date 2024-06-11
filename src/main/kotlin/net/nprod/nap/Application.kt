package net.nprod.nap

import io.ktor.server.application.*
import io.ktor.server.plugins.cors.routing.*
import net.nprod.nap.plugins.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    install(CORS) {
        //allowHost("nap-sparql:3030")
    }
    configureRouting()
}
