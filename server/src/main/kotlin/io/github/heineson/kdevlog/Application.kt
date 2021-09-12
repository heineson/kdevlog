package io.github.heineson.kdevlog

import io.github.heineson.kdevlog.web.inputRoutes
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.serialization.*
import kotlinx.serialization.Serializable


fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

fun Application.module(testing: Boolean = false) {
    install(ContentNegotiation) {
        json()
    }
    inputRoutes()
}

@Serializable
data class Test(val value: String)
