package io.github.heineson.kdevlog.model

enum class InputType { FILE, PROCESS }
enum class InputState { STOPPED, STARTED }
data class Input(val id: String, val type: InputType, val state: InputState, val value: String)
