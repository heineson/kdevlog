package io.github.heineson.kdevlog.domain

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

enum class LogEntryType { TIMESTAMP, DATE, TIME, LEVEL, MESSAGE, OTHER }

data class LogEntry(val timestamp: LocalDateTime, val level: String, val message: String)
data class TimeFormats(val dateFormat: String = "yyyy-MM-dd", val timeFormat: String = "HH:mm:ss,SSS", val timestampFormat: String = "yyyy-MM-ddTHH:mm:ss,SSS")

/**
 * Assumes TIMESTAMP is present and that MESSAGE is last
 */
fun parseEntryWithTokenization(
    entry: String,
    tokenOrder: List<LogEntryType>,
    tokenSeparator: String = " ",
    timeFormats: TimeFormats = TimeFormats()
): Result<LogEntry> {
    val tokens = entry.split(tokenSeparator, limit = tokenOrder.size)
    if (tokens.size < tokenOrder.size) {
        return Result.failure(IllegalArgumentException("Number of tokens (${tokens.size}) less than number of expected values (${tokenOrder.size})"))
    }
    val tokensWithType = tokens.zip(tokenOrder)
    val timestamp = parseTimestampToken(tokensWithType.filter { it.second in listOf(LogEntryType.TIMESTAMP, LogEntryType.DATE, LogEntryType.TIME) }, timeFormats)
        ?: return Result.failure(IllegalArgumentException("No timestamp data could be found in the entry"))

    return Result.success(LogEntry(
        timestamp,
        tokensWithType.find { it.second == LogEntryType.LEVEL }?.first ?: "",
        tokensWithType.find { it.second == LogEntryType.MESSAGE }?.first ?: ""
    ))
}

/**
 * Parses a line with regex
 */
fun parseEntryWithRegex() {

}

fun parseTimestampToken(tokens: List<Pair<String, LogEntryType>>, timeFormats: TimeFormats): LocalDateTime? {
    val typeToToken = tokens.associate { it.second to it.first }

    return when {
        typeToToken.containsKey(LogEntryType.TIMESTAMP) ->
            LocalDateTime.parse(
                typeToToken[LogEntryType.TIMESTAMP],
                DateTimeFormatter.ofPattern(timeFormats.timestampFormat))
        typeToToken.containsKey(LogEntryType.DATE) && typeToToken.containsKey(LogEntryType.TIME) -> {
            val date = LocalDate.parse(
                typeToToken[LogEntryType.DATE],
                DateTimeFormatter.ofPattern(timeFormats.dateFormat))
            val time = LocalTime.parse(
                typeToToken[LogEntryType.TIME],
                DateTimeFormatter.ofPattern(timeFormats.timeFormat))
            LocalDateTime.of(date, time)
        }
        else -> null
    }
}
