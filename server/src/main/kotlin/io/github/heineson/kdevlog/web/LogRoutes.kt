package io.github.heineson.kdevlog.web

import io.github.heineson.kdevlog.store.Filters
import io.github.heineson.kdevlog.store.LogEntryEntity
import io.github.heineson.kdevlog.store.LogStore
import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*
import kotlinx.serialization.Serializable
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss,SSS")

fun Application.logRoutes() {
    routing {
        route("/logs") {
            get {
                val localZoneOffset = OffsetDateTime.now().offset
                val from = call.request.queryParameters["from"]?.let { LocalDateTime.parse(it, formatter).toInstant(localZoneOffset) }
                val to = call.request.queryParameters["to"]?.let { LocalDateTime.parse(it, formatter).toInstant(localZoneOffset) }
                val count = call.request.queryParameters["count"]?.toIntOrNull() ?: 1000
                val offset = call.request.queryParameters["offset"]?.toLongOrNull() ?: 0

                val f = Filters(offset, count, from, to)
                val data: List<Log> = LogStore.getSome(f).map { it.toLog() }

                call.respond(data)
            }
        }
    }
}

@Serializable
data class Log(val source: String, val timestamp: Long, val level: String, val message: String, val id: String? = null)

fun LogEntryEntity.toLog(): Log {
    return Log(sourceInputId, entryData.timestamp.toEpochMilli(), entryData.level, entryData.level, id)
}