package io.github.heineson.kdevlog.input

import mu.KotlinLogging
import org.apache.commons.io.input.Tailer
import org.apache.commons.io.input.TailerListenerAdapter
import java.nio.file.Path
import kotlin.io.path.exists
import kotlin.io.path.isReadable
import kotlin.io.path.name

class FileInput(private val file: Path) : Input {
    private val log = KotlinLogging.logger {}

    private lateinit var listener: FileTailListener
    private lateinit var tailer: Tailer

    override fun start(cb: (entry: String) -> Unit) {
        if (file.exists() && file.isReadable()) {
            listener = FileTailListener(file.name, cb)
            tailer = Tailer.create(file.toFile(), listener)
            log.info { "Started listener on file $file.name" }
        } else throw IllegalArgumentException("File does not exist or is not readable: ${file.name}")
    }

    override fun close() {
        if (this::tailer.isInitialized) tailer.stop()
    }
}

private class FileTailListener(val filename: String, val cb: (entry: String) -> Unit) : TailerListenerAdapter() {
    private val log = KotlinLogging.logger {}

    override fun handle(line: String?) {
        line?.let { cb(it) }
    }

    override fun handle(ex: Exception?) = log.error(ex) { "Error tailing file: $filename" }
}
