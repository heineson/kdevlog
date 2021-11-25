package io.github.heineson.kdevlog.domain

import io.github.heineson.kdevlog.model.LogEntryData
import org.junit.jupiter.api.Test
import java.time.*
import kotlin.test.assertEquals
import kotlin.test.assertTrue

internal class LogEntryParserTest {

    val SYSLOG_ENTRY_1 = "May 22 02:17:43 server1 dhclient[916]: DHCPREQUEST on eth0 to 172.31.0.1 port 67 (xid=0x445faedb)"
    val SYSLOG_ENTRY_2 = "Sep  9 22:05:01 my-pc jetbrains-idea.desktop[85169]: #011at com.intellij.ide.IdeEventQueue.lambda\$dispatchEvent\$8(IdeEventQueue.java:442)"

    val GENERIC_ENTRY_1 = "2020-02-20 22:05:57,351 [ZAP-SpiderInitThread-2] INFO  Spider - Starting spider..."
    val GENERIC_ENTRY_2 = "2021-11-19 19:05:09.109 [compiler] INFO: leased a new session 1, session alive file: /home/jonas/dev/kdevlog/build/kotlin/sessions/kotlin-compiler-15370753167216150092.salive"
    val GENERIC_ENTRY_3 = "2021-11-03 21:55:24 status installed gnome-shell-extension-desktop-icons:all 20.04.0-3~ubuntu20.04.4"

    val zoneOffset = OffsetDateTime.now().offset

    @Test
    fun testSyslogEntry() {
        assertParsed(
            LocalDateTime.of(2021, 5, 22, 2, 17, 43),
            "",
            "DHCPREQUEST on eth0 to 172.31.0.1 port 67 (xid=0x445faedb)",
            parseEntry(SYSLOG_ENTRY_1, SYSLOG_CONFIG)
        )
    }

    @Test
    fun testSyslogEntry2() {
        assertParsed(
            LocalDateTime.of(2021, 9, 9, 22, 5, 1),
            "",
            "#011at com.intellij.ide.IdeEventQueue.lambda\$dispatchEvent\$8(IdeEventQueue.java:442)",
            parseEntry(SYSLOG_ENTRY_2, SYSLOG_CONFIG)
        )
    }

    @Test
    fun testGenericEntry1() {
        assertParsed(
            LocalDateTime.of(2020, 2, 20, 22, 5, 57, 351000000),
            "",
            "[ZAP-SpiderInitThread-2] INFO  Spider - Starting spider...",
            parseEntry(GENERIC_ENTRY_1, GENERIC_CONFIG)
        )
    }

    @Test
    fun testGenericEntry2() {
        assertParsed(
            LocalDateTime.of(2021, 11, 19, 19, 5, 9, 109000000),
            "",
            "[compiler] INFO: leased a new session 1, session alive file: /home/jonas/dev/kdevlog/build/kotlin/sessions/kotlin-compiler-15370753167216150092.salive",
            parseEntry(GENERIC_ENTRY_2, GENERIC_CONFIG)
        )
    }

    @Test
    fun testGenericEntry3() {
        assertParsed(
            LocalDateTime.of(2021, 11, 3, 21, 55, 24),
            "",
            "status installed gnome-shell-extension-desktop-icons:all 20.04.0-3~ubuntu20.04.4",
            parseEntry(GENERIC_ENTRY_3, GENERIC_CONFIG)
        )
    }

    private fun assertParsed(
        expectedTimestamp: LocalDateTime,
        expectedLevel: String,
        expectedMessage: String,
        actual: Result<LogEntryData>
    ) {
        assertTrue(actual.isSuccess)
        with(actual.getOrThrow()) {
            assertEquals(expectedTimestamp.toInstant(zoneOffset), timestamp)
            assertEquals(expectedLevel, level)
            assertEquals(expectedMessage, message)
        }
    }
}
