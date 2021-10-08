package io.github.heineson.kdevlog.web

import io.ktor.features.*
import io.ktor.http.*

fun Parameters.getMandatory(name: String) = this[name] ?: throw BadRequestException("Mandatory parameter $name missing in request")
