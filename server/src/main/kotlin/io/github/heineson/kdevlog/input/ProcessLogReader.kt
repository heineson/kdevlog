package io.github.heineson.kdevlog.input

import kotlinx.coroutines.*
import mu.KotlinLogging

class ProcessLogReader(private val command: List<String>) : LogReader() {
    private val log = KotlinLogging.logger {}
    private lateinit var process: Process
    private lateinit var job: Job

    override fun start(cb: (entry: String) -> Unit) {
        process = ProcessBuilder(command)
            .redirectOutput(ProcessBuilder.Redirect.PIPE)
            .redirectError(ProcessBuilder.Redirect.PIPE)
            .start()

        job = CoroutineScope(Dispatchers.IO).launch {
            process.inputStream.bufferedReader().forEachLine(cb)
        }
        log.info { "Process log reader created" }
    }

    override fun close() {
        job.cancel("Log reader is closing")
        process.destroy()
    }

}