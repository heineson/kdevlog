package io.github.heineson.kdevlog.domain

data class LogConfig(
    val name: String,
    val pattern: String,
    val multiline: Boolean = false,
    val timestampFormat: String = "yyyy-MM-ddTHH:mm:ss,SSS"
) {
    fun toRegex(): Regex {
        val opts: Set<RegexOption> = if (multiline) setOf(RegexOption.MULTILINE) else setOf()
        return Regex(pattern, opts)
    }
}

val SYSLOG_CONFIG = LogConfig(
    "syslog",
    "(?<timestamp>\\S+\\s+[0-9]{1,2}\\s+[0-9]+:[0-9]+:[0-9]+)\\s+(?<hostname>\\S+)\\s+(?<daemon>\\S+)(?<pid>\\[[0-9]+\\]):\\s+(?<message>.*)\\s*",
    timestampFormat = "MMM ppd HH:mm:ss"
)
