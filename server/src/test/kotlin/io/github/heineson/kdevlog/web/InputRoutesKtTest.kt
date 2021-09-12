package io.github.heineson.kdevlog.web

import io.github.heineson.kdevlog.module
import io.ktor.http.*
import io.ktor.server.testing.*
import org.junit.jupiter.api.Test
import java.nio.file.Files
import kotlin.test.assertEquals

internal class InputRoutesKtTest {
    @Test
    fun testAddNonExistentFile() {
        val filename = "file:///no/file/with/this/path"

        withTestApplication({ module(testing = true) }) {
            with(handleRequest(HttpMethod.Post, "/inputs/files") {
                addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                setBody("""
                    {
                        "uri": "$filename"
                    }
                """.trimIndent())
            }) {
                assertEquals("No file found for: $filename", response.content)
                assertEquals(400, response.status()?.value)
            }
        }
    }

    @Test
    fun addFile() {
        val file = Files.createTempFile("inputtest", ".txt")
        try {
            withTestApplication({ module(testing = true) }) {
                with(handleRequest(HttpMethod.Post, "/inputs/files") {
                    addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                    setBody("""
                    {
                        "uri": "$file"
                    }
                """.trimIndent())
                }) {
                    assertEquals(201, response.status()?.value)
                }
            }
        } finally {
            Files.deleteIfExists(file)
        }
    }
}
