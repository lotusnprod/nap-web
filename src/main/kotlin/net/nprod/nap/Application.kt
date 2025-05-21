package net.nprod.nap

import io.ktor.server.application.*
import io.ktor.server.plugins.cors.routing.*
import net.nprod.nap.plugins.*
import io.ktor.server.routing.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.plugins.contentnegotiation.*
import kotlinx.serialization.json.Json

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    install(CORS) {
        //allowHost("nap-sparql:3030")
    }
    install(ContentNegotiation) {
        json(Json {
            prettyPrint = true
            isLenient = true
        })
    }
    install(RoutingRoot)
    configureRouting()
}
