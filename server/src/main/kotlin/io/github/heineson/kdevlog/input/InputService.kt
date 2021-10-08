package io.github.heineson.kdevlog.input

import io.github.heineson.kdevlog.domain.SYSLOG_CONFIG
import io.github.heineson.kdevlog.domain.parseEntry
import io.github.heineson.kdevlog.model.Input
import io.github.heineson.kdevlog.model.InputState
import io.github.heineson.kdevlog.model.InputType
import io.github.heineson.kdevlog.model.LogEntry
import io.github.heineson.kdevlog.store.*
import mu.KotlinLogging
import java.io.FileNotFoundException
import java.nio.file.Path
import java.util.concurrent.ConcurrentHashMap
import kotlin.io.path.isRegularFile
import kotlin.io.path.notExists

class InputService(private val inputStore: Store<Input>, private val logStore: Store<LogEntry>) {
    private val logReaderInstances = ConcurrentHashMap<String, LogReader>() // TODO remove state from here
    private val log = KotlinLogging.logger {}

    fun getAll() = inputStore.getAll()
    fun get(id: String) = inputStore.get(id)

    fun addInput(source: Input): Result<Input> {
        return source.validate()?.let { Result.failure(it) } ?: Result.success(inputStore.save(source))
    }

    fun startInput(id: String): Result<Input> { // TODO merge with add?
        val input = inputStore.get(id) ?: return Result.failure(IllegalArgumentException("No input with id=$id found"))
        val reader: LogReader = LogReader.of(input)
        logReaderInstances[input.id] = reader
        reader.start(inputEntryHandler(input))
        val updated = inputStore.save(input.copy(state = InputState.STARTED))
        log.info { "Started input ${input.id}" }
        return Result.success(updated)
    }

    fun removeInput(id: String): Boolean {
        log.info { "Removing input $id" }
        logReaderInstances.remove(id)?.close()
        return inputStore.delete(id) != null
    }

    fun removeAll() {
        inputStore.getAll().forEach { removeInput(it.id) }
    }

    fun runningReaders(): List<String> = logReaderInstances.keys().toList()

    private fun inputEntryHandler(stored: Input): (entry: String) -> Unit =
        { line -> parseEntry(line, SYSLOG_CONFIG).onSuccess { logStore.save(LogEntry(stored.id, it)) } }
}

fun Input.validate(): Exception? {
    return when (type) {
        InputType.FILE -> {
            if (Path.of(value).notExists() || !Path.of(value).isRegularFile())
                FileNotFoundException("File '${this.value}' does not exist or is not a regular file")
            else null
        }
    }
}
