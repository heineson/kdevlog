package io.github.heineson.kdevlog.store

import io.github.heineson.kdevlog.domain.LogEntry
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import java.time.Instant
import java.time.temporal.ChronoUnit
import kotlin.test.*

internal class LogStoreTest {
    @AfterEach
    fun reset() {
        LogStore.clear()
    }

    @Test
    fun crudLogEntry() {
        val entity =
            LogEntryEntity("source", LogEntry(Instant.now().truncatedTo(ChronoUnit.MILLIS), "WARN", "log message"))
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
            fail("Should not be able to save an already stored entry")
        } catch (e: Exception) {
            assertTrue(e is IllegalArgumentException)
        }
        assertEquals(1, LogStore.getAll().size)

        // delete
        stored.id?.let { LogStore.delete(it) }
        assertNull(LogStore.get(stored.id!!))
        assertEquals(0, LogStore.getAll().size)
    }

    @Test
    fun getLogsWithOffsetAndLimit() {
        addLogEntries()

        val logs = LogStore.getSome(Filters(2, 2)).map { it.entryData }
        assertEquals(2, logs.size)
        assertEquals(LogEntry(Instant.ofEpochMilli(1632256718758), "INFO", "log message 2"), logs[0])
        assertEquals(LogEntry(Instant.ofEpochMilli(1632256718748), "WARN", "log message 1"), logs[1])
    }

    @Test
    fun getLogsWithTimeLimit() {
        addLogEntries()

        val logs = LogStore.getSome(Filters(from = Instant.ofEpochMilli(1632256718748), to = Instant.ofEpochMilli(1632256718759)))
            .map { it.entryData }
        assertEquals(3, logs.size)
        assertEquals(LogEntry(Instant.ofEpochMilli(1632256718759), "ERROR", "log message 5"), logs[0])
        assertEquals(LogEntry(Instant.ofEpochMilli(1632256718758), "INFO", "log message 2"), logs[1])
        assertEquals(LogEntry(Instant.ofEpochMilli(1632256718748), "WARN", "log message 1"), logs[2])
    }

    private fun addLogEntries() {
        val entities = listOf(
            LogEntryEntity("source1", LogEntry(Instant.ofEpochMilli(1632256718748), "WARN", "log message 1")),
            LogEntryEntity("source1", LogEntry(Instant.ofEpochMilli(1632256718758), "INFO", "log message 2")),
            LogEntryEntity("source1", LogEntry(Instant.ofEpochMilli(1632256718768), "WARN", "log message 3")),
            LogEntryEntity("source2", LogEntry(Instant.ofEpochMilli(1632256718747), "WARN", "log message 4")),
            LogEntryEntity("source2", LogEntry(Instant.ofEpochMilli(1632256718759), "ERROR", "log message 5")),
        )
        LogStore.saveAll(entities)
        assertEquals(5, LogStore.getAll().size)
    }
}
