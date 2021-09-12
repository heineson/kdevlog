package io.github.heineson.kdevlog.web

import io.github.heineson.kdevlog.module
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Test
import java.nio.file.Files
import java.nio.file.Path
import kotlin.test.assertContains
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
        file.toFile().deleteOnExit()

        withTestApplication({ module(testing = true) }) {
            with(postFile(file)) {
                assertEquals(201, response.status()?.value)
                response.content?.let { assertContains(it, """"id":""") }
            }
        }
    }

    @Test
    fun delete() {
        val file = Files.createTempFile("inputtest", ".txt")
        file.toFile().deleteOnExit()

        withTestApplication({ module(testing = true) }) {
            val fileResponse: File? = postFile(file).response.content?.let { Json.decodeFromString<File>(it) }
            with(handleRequest(HttpMethod.Delete, "/inputs/files/id")) {
                assertEquals(404, response.status()?.value)
            }
            with(handleRequest(HttpMethod.Delete, "/inputs/files/${fileResponse?.id}")) {
                assertEquals(204, response.status()?.value)
            }
        }
    }

    private fun TestApplicationEngine.postFile(file: Path?) =
        handleRequest(HttpMethod.Post, "/inputs/files") {
            addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            setBody("""
                    {
                        "uri": "$file"
                    }
            """.trimIndent())
        }
}
