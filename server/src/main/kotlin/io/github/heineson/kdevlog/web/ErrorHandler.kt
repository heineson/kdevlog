package io.github.heineson.kdevlog.web

import io.github.heineson.kdevlog.input.InputAlreadyExistsException
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.response.*
import kotlinx.serialization.SerializationException

// TODO perhaps add header X-Reason for frontend to read?
fun Application.errorHandler() {
    install(StatusPages) {
        exception<BadRequestException> { cause ->
            call.respond(HttpStatusCode.BadRequest, cause.message ?: "")
        }
        exception<SerializationException> { cause ->
            call.respond(HttpStatusCode.BadRequest, cause.message ?: "")
        }
        exception<InputAlreadyExistsException> { cause ->
            call.respond(HttpStatusCode.Conflict, cause.message ?: "")
        }
        exception<Throwable> { cause ->
            log.error("Internal Server Error", cause)
            call.respond(HttpStatusCode.InternalServerError)
        }
    }
}
