package io.github.heineson.kdevlog.input

import io.github.heineson.kdevlog.domain.SYSLOG_CONFIG
import io.github.heineson.kdevlog.domain.parseEntry
import io.github.heineson.kdevlog.store.*
import mu.KotlinLogging
import java.util.concurrent.ConcurrentHashMap

class InputService(private val inputStore: InputStore, private val logStore: LogStore) {
    private val inputInstances = ConcurrentHashMap<String, Input>() // TODO remove state from here
    private val log = KotlinLogging.logger {}

    fun addInput(source: InputEntity): InputEntity {
        return inputStore.save(source)
    }

    fun startInput(inputEntity: InputEntity) {
        val input: Input = Input.of(inputEntity)
        inputInstances[inputEntity.id] = input
        input.start(inputEntryHandler(inputEntity))
        log.info { "Started input ${inputEntity.id}" }
    }

    fun removeInput(id: String) {
        inputStore.delete(id)
        inputInstances.remove(id)?.close()
    }

    fun removeAll() {
        inputStore.getAll().forEach { removeInput(it.id) }
    }

    private fun inputEntryHandler(stored: InputEntity): (entry: String) -> Unit =
        { line -> parseEntry(line, SYSLOG_CONFIG).onSuccess { logStore.save(LogEntryEntity(stored.id, it)) } }
}
