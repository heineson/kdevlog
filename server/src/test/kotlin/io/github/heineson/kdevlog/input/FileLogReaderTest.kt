package io.github.heineson.kdevlog.input

import org.apache.commons.io.FileUtils
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Timeout
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption
import java.nio.file.StandardOpenOption

internal class FileLogReaderTest {
    private lateinit var tempLogFile: Path

    @BeforeEach
    fun setup() {
        tempLogFile = Files.createTempFile("syslogTest", ".out")
        FileUtils.copyFile(
            File(this.javaClass.getResource("/syslog.out").toURI()),
            tempLogFile.toFile(),
            StandardCopyOption.REPLACE_EXISTING
        )
    }

    @AfterEach
    fun tearDown() {
        FileUtils.deleteQuietly(tempLogFile.toFile())
    }

    @Test
    @Timeout(30)
    fun testTailWithAppend() {
        val result = mutableListOf<String>()
        FileLogReader(tempLogFile).use {
            it.start { line -> result.add(line) }
            while (result.size != 6) {
                Thread.sleep(10)
            }

            with(result) {
                assertEquals("Sep 10 02:17:43 server daemon[111]: message sample 1", get(0))
                assertEquals("Sep 10 02:17:48 server daemon[111]: message sample 6", get(5))
            }

            Files.writeString(
                tempLogFile,
                "Sep 10 02:17:49 server daemon[111]: message sample test\n",
                StandardOpenOption.APPEND
            )

            while (result.size != 7) {
                Thread.sleep(10)
            }

            with(result) {
                assertEquals("Sep 10 02:17:43 server daemon[111]: message sample 1", get(0))
                assertEquals("Sep 10 02:17:48 server daemon[111]: message sample 6", get(5))
                assertEquals("Sep 10 02:17:49 server daemon[111]: message sample test", get(6))
            }
        }
    }
}
