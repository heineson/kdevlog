package io.github.heineson.kdevlog

import io.github.heineson.kdevlog.web.inputRoutes
import io.github.heineson.kdevlog.web.logRoutes
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.serialization.*


fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

fun Application.module(testing: Boolean = false) {
    install(ContentNegotiation) {
        json()
    }
    inputRoutes()
    logRoutes()
}
