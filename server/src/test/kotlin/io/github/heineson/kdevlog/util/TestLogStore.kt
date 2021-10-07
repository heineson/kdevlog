package io.github.heineson.kdevlog.util

import io.github.heineson.kdevlog.model.LogEntry
import io.github.heineson.kdevlog.store.Store
import java.util.*

class TestLogStore : Store<LogEntry> {
    private val store = mutableMapOf<String, LogEntry>()

    override fun saveAll(entities: Collection<LogEntry>) = entities.forEach { save(it) }

    override fun save(entity: LogEntry): LogEntry {
        val toStore = if (entity.id == null) entity.copy(id = UUID.randomUUID().toString()) else entity
        store[toStore.id!!] = toStore
        return toStore
    }

    override fun getAll(): List<LogEntry> = store.values.toList()

    override fun get(id: String): LogEntry? = store[id]

    override fun delete(id: String): LogEntry? = store.remove(id)
}