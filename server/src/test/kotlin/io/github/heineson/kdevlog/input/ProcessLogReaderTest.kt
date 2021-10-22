package io.github.heineson.kdevlog.input

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Timeout

internal class ProcessLogReaderTest {
    @Test
    @Timeout(30)
    fun test() {
        val output = mutableListOf<String>()

        ProcessLogReader(listOf("/home/jonas/dev/personal/kdevlog/server/src/test/resources/testScript.sh")).use {
            it.start { line -> output.add(line) }

            while (output.size < 1) {
                Thread.sleep(5)
            }
            assertLinesMatch(listOf("one line"), output)

            while (output.size < 2) {
                Thread.sleep(50)
            }
            assertLinesMatch(listOf("one line", "another line"), output)
        }
    }
}