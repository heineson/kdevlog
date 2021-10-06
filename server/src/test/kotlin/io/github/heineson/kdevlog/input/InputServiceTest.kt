package io.github.heineson.kdevlog.input

import org.junit.jupiter.api.Test

internal class InputServiceTest {

    @Test
    fun testStopAndStartInput() {
        // TODO wip
    }

    class TestInput: Input {
        override fun start(cb: (entry: String) -> Unit) {
        }

        override fun close() {
        }

    }
}
