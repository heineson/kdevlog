package io.github.heineson.kdevlog.input

import io.github.heineson.kdevlog.domain.LogEntry
import io.github.heineson.kdevlog.domain.SYSLOG_CONFIG
import io.github.heineson.kdevlog.domain.parseEntry
import io.github.heineson.kdevlog.store.*
import mu.KotlinLogging
import java.nio.file.Path
import java.util.concurrent.ConcurrentHashMap

class InputService(private val inputStore: InputStore, private val logStore: LogStore) {
    private val inputInstances = ConcurrentHashMap<String, Input>()
    private val log = KotlinLogging.logger {}

    fun addAndStartReadingInput(source: InputEntity): String {
        val stored = inputStore.save(source)

        val input: Input = when (source.type) {
            InputType.FILE -> FileInput(Path.of(source.value)) { inputEntryHandler(stored)(it) }
        }
        inputInstances[stored.id] = input
        input.start()
        return stored.id
    }

    fun stopAndRemoveInput(id: String) {
        inputStore.delete(id)
        inputInstances.remove(id)?.close()
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
