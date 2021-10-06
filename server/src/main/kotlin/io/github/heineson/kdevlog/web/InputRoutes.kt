package io.github.heineson.kdevlog.web

import io.github.heineson.kdevlog.store.InputEntity
import io.github.heineson.kdevlog.store.InputStore
import io.github.heineson.kdevlog.store.InputType
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import kotlinx.serialization.Serializable
import org.kodein.di.instance
import org.kodein.di.ktor.closestDI
import java.nio.file.Path
import java.util.*
import kotlin.io.path.exists
import kotlin.io.path.isRegularFile

fun Application.inputRoutes() {
    routing {
        route("/inputs") {
            val inputStore by closestDI().instance<InputStore>()

            get {
                call.respond(inputStore.getAll().map { it.toJsonInput() })
            }
            get("{id}") {
                val id = call.parameters["id"] ?: return@get call.respond(HttpStatusCode.BadRequest)
                inputStore.get(id)?.let { call.respond(it.toJsonInput()) }
                    ?: call.respond(HttpStatusCode.NotFound)
            }

            post {
                val input = call.receive<JsonInput>()
                if (!input.validate()) {
                    return@post call.respond(HttpStatusCode.BadRequest, "No file found for: ${input.value}")
                }
                val id = UUID.randomUUID().toString()
                inputStore.save(InputEntity(id, InputType.FILE, input.value))
                call.respond(HttpStatusCode.Created, input.copy(id = id))
            }

            delete("{id}") {
                val id = call.parameters["id"] ?: return@delete call.respond(HttpStatusCode.BadRequest)
                call.respond(if (inputStore.delete(id) == null) HttpStatusCode.NotFound else HttpStatusCode.NoContent)
            }
        }
    }
}

@Serializable
data class JsonInput(val value: String, val type: InputType, val id: String? = null) {
    fun validate(): Boolean {
        return when (type) {
            InputType.FILE -> Path.of(value).exists() && Path.of(value).isRegularFile()
        }
    }
}

fun InputEntity.toJsonInput() = JsonInput(this.value, this.type, this.id)
