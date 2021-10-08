package io.github.heineson.kdevlog.web

import io.github.heineson.kdevlog.input.InputService
import io.github.heineson.kdevlog.model.Input
import io.github.heineson.kdevlog.model.InputState
import io.github.heineson.kdevlog.model.InputType
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import kotlinx.serialization.Serializable
import org.kodein.di.instance
import org.kodein.di.ktor.closestDI
import java.util.*

fun Application.inputRoutes() {
    routing {
        route("/inputs") {
            val inputService by closestDI().instance<InputService>()

            get {
                call.respond(inputService.getAll().map { it.toJsonInput() })
            }

            post {
                val input = call.receive<JsonInput>()
                val id = UUID.randomUUID().toString()
                inputService.addInput(Input(id, input.type, input.state, input.value))
                    .onFailure { throw BadRequestException(it.message ?: "", it) }
                    .onSuccess { call.respond(HttpStatusCode.Created, it.toJsonInput()) }
            }

            route("/{id}") {
                get {
                    val id = call.parameters.getMandatory("id")
                    inputService.get(id)?.let { call.respond(it.toJsonInput()) }
                        ?: call.respond(HttpStatusCode.NotFound)
                }

                delete {
                    val id = call.parameters.getMandatory("id")
                    call.respond(if (inputService.removeInput(id)) HttpStatusCode.NoContent else HttpStatusCode.NotFound)
                }

                put {
                    val id = call.parameters.getMandatory("id")
                    val state = call.receive<JsonInput>().state
                    if (state != InputState.STARTED) {
                        throw BadRequestException("Only state=${InputState.STARTED} is allowed")
                    }
                    inputService.startInput(id)
                        .onSuccess { call.respond(it.toJsonInput()) }
                        .onFailure { throw it }
                }
            }
        }
    }
}

@Serializable
data class JsonInput(val id: String? = null, val value: String, val type: InputType, val state: InputState = InputState.STOPPED)

fun Input.toJsonInput() = JsonInput(this.id, this.value, this.type, this.state)
