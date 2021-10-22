package io.github.heineson.kdevlog.input

import mu.KotlinLogging
import java.io.IOException


class ProcessLogReader(private val command: List<String>) : LogReader() {
    private val log = KotlinLogging.logger {}
    private lateinit var process: Process
    private lateinit var ioThread: Thread

    override fun start(cb: (entry: String) -> Unit) {
        process = ProcessBuilder(command)
            .redirectOutput(ProcessBuilder.Redirect.PIPE)
            .redirectError(ProcessBuilder.Redirect.PIPE)
            .start()

        ioThread = object : Thread() {
            override fun run() {
                println("STARTED THREAD")
                try {
                    process.inputStream.bufferedReader().forEachLine {
                        println("ADD: $it")
                        cb(it)
                        yield()
                    }
                } catch (exception: IOException) {
                    System.err.println("Fatal Error: " + exception.message)
                }
            }
        }
        ioThread.start()
    }

    override fun close() {
        if (ioThread.isAlive) ioThread.stop()
        process.destroy()
    }

}