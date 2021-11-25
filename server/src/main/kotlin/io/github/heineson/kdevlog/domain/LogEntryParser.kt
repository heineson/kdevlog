package io.github.heineson.kdevlog.domain

import io.github.heineson.kdevlog.model.LogEntryData
import java.time.Instant
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

/**
 * Parses a line with regex
 */
fun parseEntry(entry: String, config: LogFormat): Result<LogEntryData> {
    return config.regex.matchEntire(entry)?.groups?.let {
        val ts = it["timestamp"]?.value
            ?.let { v -> parseTimestamp(v, config.timestampFormatter, config.timestampMapper) }
            ?.getOrElse { x -> return@let Result.failure(x) }
            ?: return Result.failure(IllegalArgumentException("Could not parse timestamp"))
        val level = if (config.pattern.contains("<level>")) it["level"]?.value ?: "" else ""
        val body = it["message"]?.value ?: return Result.failure(IllegalArgumentException("Could not find message body"))
        Result.success(LogEntryData(ts, level, body))
    } ?: Result.failure(IllegalArgumentException("Could not parse log entry"))
}

private fun parseTimestamp(token: String, formatter: DateTimeFormatter, mapper: (String) -> String): Result<Instant> {
    val localZoneOffset = OffsetDateTime.now().offset
    return try {
        Result.success(LocalDateTime.parse(mapper(token), formatter).toInstant(localZoneOffset))
    } catch (e: DateTimeParseException) {
        Result.failure(e)
    }
}
