package io.github.heineson.kdevlog.input

interface Input: AutoCloseable {
    fun start(cb: (entry: String) -> Unit)
}
