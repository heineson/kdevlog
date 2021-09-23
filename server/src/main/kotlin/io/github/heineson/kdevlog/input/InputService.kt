package io.github.heineson.kdevlog.input

import io.github.heineson.kdevlog.domain.LogEntry
import io.github.heineson.kdevlog.domain.SYSLOG_CONFIG
import io.github.heineson.kdevlog.domain.parseEntry
import io.github.heineson.kdevlog.store.*
import mu.KotlinLogging
import java.nio.file.Path

class InputService(private val inputStore: InputStore, private val logStore: LogStore) {
    private val log = KotlinLogging.logger {}

    fun addAndStartReadingInput(source: InputEntity) {
        val stored = InputStore.save(source)

        val input: Input = when (source.type) {
            InputType.FILE -> FileInput(Path.of(source.value)) { inputEntryHandler(stored)(it) }
        }
    }

    fun stopAndRemoveInput(id: String) {
        inputStore.delete(id)
        TODO("Stop reading input")
    }

    fun close() {
        inputStore.getAll().forEach { stopAndRemoveInput(it.id) }
    }

    private fun inputEntryHandler(stored: InputEntity): (String) -> () -> Result<LogEntry> {
        val inputHandler = { line: String ->
            {
                // TODO find config automatically based on the first few lines received?
                parseEntry(line, SYSLOG_CONFIG).onSuccess { logStore.save(LogEntryEntity(stored.id, it)) }
            }
        }
        return inputHandler
    }
}
