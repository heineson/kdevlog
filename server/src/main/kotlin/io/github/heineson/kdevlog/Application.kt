package io.github.heineson.kdevlog

import io.github.heineson.kdevlog.input.InputService
import io.github.heineson.kdevlog.store.InputStore
import io.github.heineson.kdevlog.store.LogStore
import io.github.heineson.kdevlog.web.errorHandler
import io.github.heineson.kdevlog.web.inputRoutes
import io.github.heineson.kdevlog.web.logRoutes
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.serialization.*
import org.kodein.di.bind
import org.kodein.di.instance
import org.kodein.di.ktor.closestDI
import org.kodein.di.ktor.di
import org.kodein.di.singleton


fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

fun Application.module(testing: Boolean = false) {
    di {
        bind { singleton { InputStore() } }
        bind { singleton { LogStore() } }
        bind { singleton { InputService(instance(), instance()) } }
    }
    install(ContentNegotiation) {
        json()
    }
    install(CORS) {
        anyHost()
        method(HttpMethod.Put)
        allowNonSimpleContentTypes = true
        allowSameOrigin = true
    }
    inputRoutes()
    logRoutes()
    errorHandler()

    environment.monitor.subscribe(ApplicationStopped){
        log.info("Shutting down...")
        val inputService by closestDI().instance<InputService>()
        inputService.removeAll()
    }
}
