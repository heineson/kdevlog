package io.github.heineson.kdevlog.input

import io.github.heineson.kdevlog.model.Input
import io.github.heineson.kdevlog.model.InputState
import io.github.heineson.kdevlog.model.InputType
import io.github.heineson.kdevlog.util.TestInputStore
import io.github.heineson.kdevlog.util.TestLogStore
import org.apache.commons.io.FileUtils
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Timeout
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption
import kotlin.io.path.pathString
import kotlin.test.assertEquals

internal class InputServiceTest {
    private lateinit var tempLogFile: Path
    private lateinit var service: InputService
    private lateinit var inputStore: TestInputStore
    private lateinit var logStore: TestLogStore

    @BeforeEach
    fun setup() {
        tempLogFile = Files.createTempFile("syslogTest", ".out")
        FileUtils.copyFile(
            File(this.javaClass.getResource("/syslog.out").toURI()),
            tempLogFile.toFile(),
            StandardCopyOption.REPLACE_EXISTING
        )
        inputStore = TestInputStore()
        logStore = TestLogStore()
        service = InputService(inputStore, logStore)
    }

    @AfterEach
    fun tearDown() {
        service.removeAll()
        FileUtils.deleteQuietly(tempLogFile.toFile())
    }

    @Test
    @Timeout(30)
    fun testInputLifecycle() {
        val input = Input("input1", InputType.FILE, InputState.STOPPED, tempLogFile.pathString)
        val entriesInLogFile = 6

        // ADD
        service.addInput(input)
        with(inputStore.getAll()) {
            assertEquals(1, this.size)
            assertEquals(input.id, this[0].id)
            assertEquals(0, service.runningReaders().size)
            assertEquals(InputState.STOPPED, this[0].state)
        }

        // START
        service.startInput(input.id)
        with(service.runningReaders()) {
            assertEquals(1, this.size)
            assertEquals(input.id, this[0])
        }
        assertEquals(InputState.STARTED, service.get(input.id)?.state)

        // COLLECT LOG ENTRIES
        while (logStore.getAll().size < entriesInLogFile) {
            Thread.sleep(50)
        }
        assertEquals(entriesInLogFile, logStore.getAll().size)

        // REMOVE
        service.removeInput(input.id)
        assertEquals(0, inputStore.getAll().size)
        assertEquals(0, service.runningReaders().size)
    }

}
