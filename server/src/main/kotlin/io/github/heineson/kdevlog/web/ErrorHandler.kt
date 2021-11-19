package io.github.heineson.kdevlog.web

import io.github.heineson.kdevlog.input.InputAlreadyExistsException
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.response.*
import kotlinx.serialization.SerializationException

const val X_REASON_HEADER = "X-Reason"

fun Application.errorHandler() {
    install(StatusPages) {
        respondWithReasonHeader<BadRequestException>(HttpStatusCode.BadRequest)
        respondWithReasonHeader<SerializationException>(HttpStatusCode.BadRequest)
        respond<InputAlreadyExistsException>(HttpStatusCode.Conflict)

        exception<Throwable> { cause ->
            log.error("Internal Server Error", cause)
            call.respond(HttpStatusCode.InternalServerError)
        }
    }
}

inline fun <reified T : Throwable> StatusPages.Configuration.respond(status: HttpStatusCode) {
    exception<T> { cause ->
        call.respond(status, cause.message ?: "")
    }
}

inline fun <reified T : Throwable> StatusPages.Configuration.respondWithReasonHeader(status: HttpStatusCode) {
    exception<T> { cause ->
        cause.message?.let { call.response.headers.append(X_REASON_HEADER, it) }
        call.respond(status, cause.message ?: "")
    }
}
