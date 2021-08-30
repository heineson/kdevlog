package io.github.heineson.kdevlog.domain

import java.time.LocalDateTime

enum class LogEntryTypes { TIMESTAMP, LEVEL, MESSAGE }

data class LogEntry(val timestamp: LocalDateTime, val level: String, val message: String)

fun parseEntry(entry: String, pattern: Regex, order: List<LogEntryTypes>): LogEntry {
    val entries = pattern.findAll(entry)
}
