package io.github.heineson.kdevlog.domain

import kotlin.test.Test

internal class LogParserTest {

    val SYSLOG_ENTRY = "May 22 02:17:43 server1 dhclient[916]: DHCPREQUEST on eth0 to 172.31.0.1 port 67 (xid=0x445faedb)"
    val LOGBACK_1 = ""

    @Test
    fun testSyslogEntry() {
        val parsed = parseEntryWithTokenization(SYSLOG_ENTRY, listOf(LogEntryType.DATE, LogEntryType.TIME, LogEntryType.OTHER, LogEntryType.OTHER, LogEntryType.MESSAGE))
    }
}
