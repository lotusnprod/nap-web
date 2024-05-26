package net.nprod.nap

import io.ktor.server.application.*
import net.nprod.nap.plugins.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    configureRouting()
}
