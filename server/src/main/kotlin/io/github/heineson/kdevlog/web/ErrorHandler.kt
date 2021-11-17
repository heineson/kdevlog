package io.github.heineson.kdevlog.web

import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.response.*

fun Application.errorHandler() {
    install(StatusPages) {
        exception<BadRequestException> { cause ->
            call.respond(HttpStatusCode.BadRequest, cause.message ?: "")
        }
        exception<FileAlreadyExistsException> { cause ->
            call.respond(HttpStatusCode.Conflict, cause.message ?: "")
        }
        exception<Throwable> { cause ->
            log.error("Internal Server Error", cause)
            call.respond(HttpStatusCode.InternalServerError)
        }
    }
}
