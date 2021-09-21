package io.github.heineson.kdevlog.store

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.github.heineson.kdevlog.domain.LogEntry
import mu.KotlinLogging
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.`java-time`.timestamp
import org.jetbrains.exposed.sql.transactions.transaction

object LogStore : Store<LogEntryEntity> {
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

    override fun saveAll(entities: Collection<LogEntryEntity>) {
        TODO("Not yet implemented")
    }

    override fun save(entity: LogEntryEntity): LogEntryEntity {
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

    override fun getAll(): List<LogEntryEntity> {
        return transaction {
            Logs.selectAll().map(toEntity())
        }
    }

    override fun get(id: String): LogEntryEntity? {
        return transaction {
            Logs.select { Logs.id eq id.toInt() }.map(toEntity()).firstOrNull()
        }
    }

    override fun delete(id: String): LogEntryEntity? {
        transaction {
            Logs.deleteWhere { Logs.id eq id.toInt() }
        }
        return null
    }

    private fun toEntity(): (ResultRow) -> LogEntryEntity =
        {
            LogEntryEntity(
                it[Logs.sourceInputId],
                LogEntry(it[Logs.timestamp], it[Logs.level], it[Logs.message]),
                it[Logs.id].toString()
            )
        }

}

data class LogEntryEntity(val sourceInputId: String, val entryData: LogEntry, val id: String? = null)

private object Logs : IntIdTable() {
    val timestamp = timestamp("timestamp")
    val level = varchar("level", 12)
    val message = varchar("message", 5000)
    val sourceInputId = varchar("sourceInputId", 36)
}
