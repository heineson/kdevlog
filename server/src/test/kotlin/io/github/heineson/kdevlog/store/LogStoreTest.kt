package io.github.heineson.kdevlog.store

import io.github.heineson.kdevlog.domain.LogEntry
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.time.Instant
import java.time.temporal.ChronoUnit

internal class LogStoreTest {
    @Test
    fun crudLogEntry() {
        val entity = LogEntryEntity("source", LogEntry(Instant.now().truncatedTo(ChronoUnit.MILLIS), "WARN", "log message"))
        assertNull(entity.id)

        // create
        val stored = LogStore.save(entity)
        assertNotNull(stored.id)

        // read
        val read = LogStore.get(stored.id!!)
        read?.let {
            assertEquals("source", it.sourceInputId)
            assertEquals("WARN", it.entryData.level)
            assertEquals(entity.entryData.timestamp, it.entryData.timestamp)
        } ?: fail("Read should not return null")

        // update - no update, log entries are immutable
        try {
            LogStore.save(stored)
            fail<Void>("Should not be able to save an already stored entry")
        } catch (e: Exception) {
            assertTrue(e is IllegalArgumentException)
        }
        assertEquals(1, LogStore.getAll().size)

        // delete
        stored.id?.let { LogStore.delete(it) }
        assertNull(LogStore.get(stored.id!!))
        assertEquals(0, LogStore.getAll().size)
    }
}
