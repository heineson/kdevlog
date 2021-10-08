package io.github.heineson.kdevlog.store

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.github.heineson.kdevlog.model.LogEntryData
import io.github.heineson.kdevlog.model.LogEntry
import mu.KotlinLogging
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.`java-time`.timestamp
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.Instant

class LogStore : Store<LogEntry> {
    private val log = KotlinLogging.logger {}

    init {
        val config = HikariConfig().apply {
            jdbcUrl = "jdbc:sqlite:file:logs?mode=memory&cache=shared"
            maximumPoolSize = 5
        }
        Database.connect(HikariDataSource(config))
        transaction {
            SchemaUtils.create(Logs)
        }
        log.info { "Created in-memory sqlite log database" }
    }

    override fun saveAll(entities: Collection<LogEntry>) {
        transaction {
            Logs.batchInsert(entities.filter { it.id == null }) {
                this[Logs.timestamp] = it.entryData.timestamp
                this[Logs.level] = it.entryData.level
                this[Logs.message] = it.entryData.message
                this[Logs.sourceInputId] = it.sourceInputId
            }
        }
    }

    override fun save(entity: LogEntry): LogEntry {
        entity.id?.let {
            throw IllegalArgumentException("You can't save an already stored entry")
        }
        val id = transaction {
            Logs.insertAndGetId {
                it[timestamp] = entity.entryData.timestamp
                it[level] = entity.entryData.level
                it[message] = entity.entryData.message
                it[sourceInputId] = entity.sourceInputId
            }
        }
        return entity.copy(id = id.toString())
    }

    override fun getAll(): List<LogEntry> {
        return transaction {
            Logs.selectAll().map(toEntity())
        }
    }

    override fun get(id: String): LogEntry? {
        return transaction {
            Logs.select { Logs.id eq id.toInt() }.map(toEntity()).firstOrNull()
        }
    }

    fun getSome(filters: Filters = Filters()): List<LogEntry> {
        val q = Logs.selectAll()
        filters.from?.let { q.andWhere { Logs.timestamp greaterEq it } }
        filters.to?.let { q.andWhere { Logs.timestamp lessEq it } }
        q.orderBy(Logs.timestamp to SortOrder.DESC).limit(filters.limit, offset = filters.offset)

        return transaction {
            q.map(toEntity())
        }
    }

    override fun delete(id: String): LogEntry? {
        transaction {
            Logs.deleteWhere { Logs.id eq id.toInt() }
        }
        return null
    }

    fun clear() {
        val count = transaction {
            Logs.deleteAll()
        }
        log.info { "Logs table cleared, $count entries removed" }
    }

    private fun toEntity(): (ResultRow) -> LogEntry =
        {
            LogEntry(
                it[Logs.sourceInputId],
                LogEntryData(it[Logs.timestamp], it[Logs.level], it[Logs.message]),
                it[Logs.id].toString()
            )
        }

}

data class Filters(val offset: Long = 0, val limit: Int = 1000, val from: Instant? = null, val to: Instant? = null)

private object Logs : IntIdTable() {
    val timestamp = timestamp("timestamp")
    val level = varchar("level", 12)
    val message = varchar("message", 5000)
    val sourceInputId = varchar("sourceInputId", 36)
}
