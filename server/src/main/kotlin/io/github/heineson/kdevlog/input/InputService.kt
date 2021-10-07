package io.github.heineson.kdevlog.input

import io.github.heineson.kdevlog.domain.SYSLOG_CONFIG
import io.github.heineson.kdevlog.domain.parseEntry
import io.github.heineson.kdevlog.model.Input
import io.github.heineson.kdevlog.model.LogEntry
import io.github.heineson.kdevlog.store.*
import mu.KotlinLogging
import java.util.concurrent.ConcurrentHashMap

class InputService(private val inputStore: Store<Input>, private val logStore: Store<LogEntry>) {
    private val logReaderInstances = ConcurrentHashMap<String, LogReader>() // TODO remove state from here
    private val log = KotlinLogging.logger {}

    fun getAll() = inputStore.getAll()
    fun get(id: String) = inputStore.get(id)

    fun addInput(source: Input): Input = inputStore.save(source)

    fun startInput(input: Input) {
        val reader: LogReader = LogReader.of(input)
        logReaderInstances[input.id] = reader
        reader.start(inputEntryHandler(input))
        log.info { "Started input ${input.id}" }
    }

    fun removeInput(id: String): Boolean {
        logReaderInstances.remove(id)?.close()
        return inputStore.delete(id) != null
    }

    fun removeAll() {
        inputStore.getAll().forEach { removeInput(it.id) }
    }

    fun runningReaders(): List<String> {
        return logReaderInstances.keys().toList()
    }

    private fun inputEntryHandler(stored: Input): (entry: String) -> Unit =
        { line -> parseEntry(line, SYSLOG_CONFIG).onSuccess { logStore.save(LogEntry(stored.id, it)) } }
}
