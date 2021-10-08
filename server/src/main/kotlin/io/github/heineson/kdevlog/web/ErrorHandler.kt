package io.github.heineson.kdevlog.web

import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.response.*
import mu.KotlinLogging
import java.io.FileNotFoundException

fun Application.errorHandler() {
    install(StatusPages) {
        exception<BadRequestException> { cause ->
            call.respond(HttpStatusCode.BadRequest, cause.message ?: "")
        }
        exception<Throwable> { cause ->
            log.error("Internal Server Error", cause)
            call.respond(HttpStatusCode.InternalServerError)
        }
    }
}
