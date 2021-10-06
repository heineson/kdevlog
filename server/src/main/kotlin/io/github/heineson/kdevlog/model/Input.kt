package io.github.heineson.kdevlog.model

enum class InputType { FILE }
data class Input(val id: String, val type: InputType, val value: String)