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
import java.nio.file.Path
import java.util.*
import kotlin.io.path.isRegularFile
import kotlin.io.path.notExists

fun Application.inputRoutes() {
    routing {
        route("/inputs/files") {
            get {
                call.respond(InputStore.getAll().map { File(it.value, it.id) })
            }
            get("{id}") {
                val id = call.parameters["id"] ?: return@get call.respond(HttpStatusCode.BadRequest)
                InputStore.get(id)?.let { call.respond(File(it.value, it.id)) }
                    ?: call.respond(HttpStatusCode.NotFound)
            }

            post {
                val file = call.receive<File>()
                if (Path.of(file.uri).notExists() || !Path.of(file.uri).isRegularFile()) {
                    return@post call.respond(HttpStatusCode.BadRequest, "No file found for: ${file.uri}")
                }
                val id = UUID.randomUUID().toString()
                InputStore.save(InputEntity(id, InputType.FILE, file.uri))
                call.respond(HttpStatusCode.Created, file.copy(id = id))
            }

            delete("{id}") {
                val id = call.parameters["id"] ?: return@delete call.respond(HttpStatusCode.BadRequest)
                call.respond(if (InputStore.delete(id) == null) HttpStatusCode.NotFound else HttpStatusCode.NoContent)
            }
        }
    }
}

@Serializable
data class File(val uri: String, val id: String? = null)
