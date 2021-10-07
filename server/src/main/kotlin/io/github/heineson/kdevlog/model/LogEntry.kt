package io.github.heineson.kdevlog.model

import io.github.heineson.kdevlog.domain.LogEntryData

data class LogEntry(val sourceInputId: String, val entryData: LogEntryData, val id: String? = null)