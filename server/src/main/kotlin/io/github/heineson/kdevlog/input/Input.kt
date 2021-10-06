package io.github.heineson.kdevlog.input

import io.github.heineson.kdevlog.store.InputEntity
import io.github.heineson.kdevlog.store.InputType
import java.nio.file.Path

sealed class Input {
    abstract fun start(cb: (entry: String) -> Unit)
    abstract fun close()

    companion object {
        fun of(model: InputEntity): Input = when (model.type) {
            InputType.FILE -> FileInput(Path.of(model.value))
        }
    }
}
