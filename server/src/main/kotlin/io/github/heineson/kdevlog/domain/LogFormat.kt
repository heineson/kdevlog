package io.github.heineson.kdevlog.domain

import java.time.Year
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder
import java.time.temporal.ChronoField

fun createDefaultDateTimeFormatter(format: String): DateTimeFormatter = DateTimeFormatterBuilder()
    .appendPattern(format)
    .parseDefaulting(ChronoField.YEAR_OF_ERA, Year.now().value.toLong()) // Some timestamps do not have year
    .toFormatter()

data class LogFormat(
    val name: String,
    val pattern: String,
    val multiline: Boolean = false,
    val timestampFormat: String = "yyyy-MM-ddTHH:mm:ss,SSS",
    val timestampMapper: (String) -> String = { it },
    val timestampFormatter: DateTimeFormatter = createDefaultDateTimeFormatter(timestampFormat)
) {
    val regex: Regex by lazy {
        val opts: Set<RegexOption> = if (multiline) setOf(RegexOption.MULTILINE) else setOf()
        Regex(pattern, opts)
    }
}

val SYSLOG_CONFIG by lazy { LogFormat(
    "syslog",
    "(?<timestamp>\\S+\\s+[0-9]{1,2}\\s+[0-9]+:[0-9]+:[0-9]+)\\s+(?<hostname>\\S+)\\s+(?<daemon>\\S+)(?<pid>\\[[0-9]+\\]):\\s+(?<message>.*)\\s*",
    timestampFormat = "MMM ppd HH:mm:ss"
)}

val GENERIC_CONFIG by lazy {
    LogFormat(
        "generic",
        "(?<timestamp>[0-9]{4}-[0-9]{2}-[0-9]{2}\\s+[0-9]+:[0-9]+:[0-9]+([,.][0-9]{3})?)\\s+(?<message>.*)\\s*",
        timestampFormat = "yyyy-MM-dd HH:mm:ss,SSS",
        timestampMapper = { ts ->
            ts.replace('.', ',').let { if (it.contains(',')) it else "$it,000" }
        }
    )
}
/*
2021-11-19 19:05:08.926
2021-05-16 21:52:53,631
2021-11-03 21:54:41

 */
