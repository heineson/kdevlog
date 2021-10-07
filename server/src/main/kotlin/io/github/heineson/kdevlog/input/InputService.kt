package io.github.heineson.kdevlog.input

import io.github.heineson.kdevlog.domain.SYSLOG_CONFIG
import io.github.heineson.kdevlog.domain.parseEntry
import io.github.heineson.kdevlog.model.Input
import io.github.heineson.kdevlog.model.LogEntry
import io.github.heineson.kdevlog.store.*
import mu.KotlinLogging
import java.util.concurrent.ConcurrentHashMap

class InputService(private val inputStore: InputStore, private val logStore: LogStore) {
    private val logReaderInstances = ConcurrentHashMap<String, LogReader>() // TODO remove state from here
    private val log = KotlinLogging.logger {}

    fun addInput(source: Input): Input {
        return inputStore.save(source)
    }

    fun startInput(input: Input) {
        val reader: LogReader = LogReader.of(input)
        logReaderInstances[input.id] = reader
        reader.start(inputEntryHandler(input))
        log.info { "Started input ${input.id}" }
    }

    fun removeInput(id: String) {
        logReaderInstances.remove(id)?.close()
        inputStore.delete(id)
    }

    fun removeAll() {
        inputStore.getAll().forEach { removeInput(it.id) }
    }

    private fun inputEntryHandler(stored: Input): (entry: String) -> Unit =
        { line -> parseEntry(line, SYSLOG_CONFIG).onSuccess { logStore.save(LogEntry(stored.id, it)) } }
}
