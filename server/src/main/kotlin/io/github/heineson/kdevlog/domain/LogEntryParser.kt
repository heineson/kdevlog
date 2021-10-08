package io.github.heineson.kdevlog.domain

import io.github.heineson.kdevlog.model.LogEntryData
import java.time.*
import java.time.format.DateTimeFormatterBuilder
import java.time.format.DateTimeParseException
import java.time.temporal.ChronoField

/**
 * Parses a line with regex
 */
fun parseEntry(entry: String, config: LogFormat): Result<LogEntryData> {
    // TODO .toRegex() will compile a pattern on each call so this is probably expensive
    return config.toRegex().matchEntire(entry)?.groups?.let {
        val ts = it["timestamp"]?.value
            ?.let { v -> parseTimestamp(v, config.timestampFormat) }
            ?.getOrElse { x -> return@let Result.failure(x) }
            ?: return Result.failure(IllegalArgumentException("Could not parse timestamp"))
        val level = if (config.pattern.contains("<level>")) it["level"]?.value ?: "" else ""
        val body = it["message"]?.value ?: return Result.failure(IllegalArgumentException("Could not find message body"))
        Result.success(LogEntryData(ts, level, body))
    } ?: Result.failure(IllegalArgumentException("Could not parse log entry"))
}

// TODO this is probably expensive as a formatter is re-created for each line
private fun parseTimestamp(token: String, format: String): Result<Instant> {
    val localZoneOffset = OffsetDateTime.now().offset
    return try {
        val formatter = DateTimeFormatterBuilder()
            .appendPattern(format)
            .parseDefaulting(ChronoField.YEAR, Year.now().value.toLong()) // Some timestamps do not have year
            .toFormatter()
        Result.success(LocalDateTime.parse(token, formatter).toInstant(localZoneOffset))
    } catch (e: DateTimeParseException) {
        Result.failure(e)
    }
}
