package io.github.heineson.kdevlog.input

import io.github.heineson.kdevlog.model.Input
import io.github.heineson.kdevlog.model.InputType
import java.nio.file.Path

sealed class LogReader : AutoCloseable {
    abstract fun start(cb: (entry: String) -> Unit)

    companion object {
        fun of(model: Input): LogReader = when (model.type) {
            InputType.FILE -> FileLogReader(Path.of(model.value))
            InputType.PROCESS -> ProcessLogReader(model.value.split(Regex("\\s+")))
        }
    }
}
