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
            post {
                val file = call.receive<File>()
                if (Path.of(file.uri).notExists() || !Path.of(file.uri).isRegularFile()) {
                    call.respond(HttpStatusCode.BadRequest, "No file found for: ${file.uri}")
                }
                InputStore.save(InputEntity(UUID.randomUUID().toString(), InputType.FILE))
                call.respond(HttpStatusCode.Created)
            }
            delete("{id}") {

            }
        }
    }
}

@Serializable
data class File(val uri: String)
