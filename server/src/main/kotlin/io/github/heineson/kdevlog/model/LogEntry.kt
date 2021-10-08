package io.github.heineson.kdevlog.model

import java.time.Instant

data class LogEntry(val sourceInputId: String, val entryData: LogEntryData, val id: String? = null)
data class LogEntryData(val timestamp: Instant, val level: String, val message: String)