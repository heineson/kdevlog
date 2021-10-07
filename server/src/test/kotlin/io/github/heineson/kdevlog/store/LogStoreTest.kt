package io.github.heineson.kdevlog.store

import io.github.heineson.kdevlog.domain.LogEntryData
import io.github.heineson.kdevlog.model.LogEntry
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import java.time.Instant
import java.time.temporal.ChronoUnit
import kotlin.test.*

internal class LogStoreTest {
    @AfterEach
    fun reset() {
        LogStore().clear()
    }

    @Test
    fun crudLogEntry() {
        val entity =
            LogEntry("source", LogEntryData(Instant.now().truncatedTo(ChronoUnit.MILLIS), "WARN", "log message"))
        assertNull(entity.id)

        with(LogStore()) {
            // create
            val stored = save(entity)
            assertNotNull(stored.id)

            // read
            val read = get(stored.id!!)
            read?.let {
                assertEquals("source", it.sourceInputId)
                assertEquals("WARN", it.entryData.level)
                assertEquals(entity.entryData.timestamp, it.entryData.timestamp)
            } ?: fail("Read should not return null")

            // update - no update, log entries are immutable
            try {
                save(stored)
                fail("Should not be able to save an already stored entry")
            } catch (e: Exception) {
                assertTrue(e is IllegalArgumentException)
            }
            assertEquals(1, getAll().size)

            // delete
            stored.id?.let { delete(it) }
            assertNull(get(stored.id!!))
            assertEquals(0, getAll().size)
        }
    }

    @Test
    fun getLogsWithOffsetAndLimit() {
        with(LogStore()) {
            addLogEntries(this)

            val logs = getSome(Filters(2, 2)).map { it.entryData }
            assertEquals(2, logs.size)
            assertEquals(LogEntryData(Instant.ofEpochMilli(1632256718758), "INFO", "log message 2"), logs[0])
            assertEquals(LogEntryData(Instant.ofEpochMilli(1632256718748), "WARN", "log message 1"), logs[1])
        }
    }

    @Test
    fun getLogsWithTimeLimit() {
        with(LogStore()) {
            addLogEntries(this)

            val filters = Filters(
                from = Instant.ofEpochMilli(1632256718748),
                to = Instant.ofEpochMilli(1632256718759)
            )
            val logs = getSome(filters).map { it.entryData }
            assertEquals(3, logs.size)
            assertEquals(LogEntryData(Instant.ofEpochMilli(1632256718759), "ERROR", "log message 5"), logs[0])
            assertEquals(LogEntryData(Instant.ofEpochMilli(1632256718758), "INFO", "log message 2"), logs[1])
            assertEquals(LogEntryData(Instant.ofEpochMilli(1632256718748), "WARN", "log message 1"), logs[2])
        }
    }

    private fun addLogEntries(logStore: LogStore) {
        val entities = listOf(
            LogEntry("source1", LogEntryData(Instant.ofEpochMilli(1632256718748), "WARN", "log message 1")),
            LogEntry("source1", LogEntryData(Instant.ofEpochMilli(1632256718758), "INFO", "log message 2")),
            LogEntry("source1", LogEntryData(Instant.ofEpochMilli(1632256718768), "WARN", "log message 3")),
            LogEntry("source2", LogEntryData(Instant.ofEpochMilli(1632256718747), "WARN", "log message 4")),
            LogEntry("source2", LogEntryData(Instant.ofEpochMilli(1632256718759), "ERROR", "log message 5")),
        )
        logStore.saveAll(entities)
        assertEquals(5, logStore.getAll().size)
    }
}
