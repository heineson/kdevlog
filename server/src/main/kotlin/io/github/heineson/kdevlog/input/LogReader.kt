package io.github.heineson.kdevlog.input

import io.github.heineson.kdevlog.store.InputEntity
import io.github.heineson.kdevlog.store.InputType
import java.nio.file.Path

sealed class LogReader : AutoCloseable {
    abstract fun start(cb: (entry: String) -> Unit)

    companion object {
        fun of(model: InputEntity): LogReader = when (model.type) {
            InputType.FILE -> FileLogReader(Path.of(model.value))
        }
    }
}
