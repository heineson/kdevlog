package io.github.heineson.kdevlog.domain

import org.junit.jupiter.api.Test
import java.time.*
import kotlin.test.assertEquals
import kotlin.test.assertTrue

internal class LogEntryParserTest {

    val SYSLOG_ENTRY_1 = "May 22 02:17:43 server1 dhclient[916]: DHCPREQUEST on eth0 to 172.31.0.1 port 67 (xid=0x445faedb)"
    val SYSLOG_ENTRY_2 = "Sep  9 22:05:01 my-pc jetbrains-idea.desktop[85169]: #011at com.intellij.ide.IdeEventQueue.lambda\$dispatchEvent\$8(IdeEventQueue.java:442)"

    val zoneOffset = OffsetDateTime.now().offset

    @Test
    fun testSyslogEntry() {
        val parsed = parseEntry(SYSLOG_ENTRY_1, SYSLOG_CONFIG)
        println(parsed)
        assertTrue(parsed.isSuccess)
        with(parsed.getOrThrow()) {
            assertEquals(LocalDateTime.of(2021, 5, 22, 2, 17, 43).toInstant(zoneOffset), timestamp)
            assertEquals("", level)
            assertEquals("DHCPREQUEST on eth0 to 172.31.0.1 port 67 (xid=0x445faedb)", message)
        }
    }

    @Test
    fun testSyslogEntry2() {
        val parsed = parseEntry(SYSLOG_ENTRY_2, SYSLOG_CONFIG)
        println(parsed)
        assertTrue(parsed.isSuccess)
        with(parsed.getOrThrow()) {
            assertEquals(LocalDateTime.of(2021, 9, 9, 22, 5, 1).toInstant(zoneOffset), timestamp)
            assertEquals("", level)
            assertEquals("#011at com.intellij.ide.IdeEventQueue.lambda\$dispatchEvent\$8(IdeEventQueue.java:442)", message)
        }
    }
}
