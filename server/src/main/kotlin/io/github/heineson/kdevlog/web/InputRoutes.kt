package io.github.heineson.kdevlog.web

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import kotlinx.serialization.Serializable
import java.nio.file.Path
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
            }
            delete("{id}") {

            }
        }
    }
}

@Serializable
data class File(val uri: String)
