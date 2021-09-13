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
    fun postFile_BadRequestIfFileNotFound() {
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
    fun postFile_ShouldReturnIdIfSuccessful() {
        val file = createTempFile()

        withTestApplication({ module(testing = true) }) {
            with(postFile(file)) {
                assertEquals(201, response.status()?.value)
                response.content?.let { assertContains(it, """"id":""") }
            }
        }
    }

    @Test
    fun deleteFile() {
        val file = createTempFile()

        withTestApplication({ module(testing = true) }) {
            val fileResponse: File? = addFile(file)
            with(handleRequest(HttpMethod.Delete, "/inputs/files/id")) {
                assertEquals(404, response.status()?.value)
            }
            with(handleRequest(HttpMethod.Delete, "/inputs/files/${fileResponse?.id}")) {
                assertEquals(204, response.status()?.value)
            }
        }
    }

    @Test
    fun getAll() {
        withTestApplication({ module(testing = true) }) {
            with(handleRequest(HttpMethod.Get, "/inputs/files")) {
                assertEquals(200, response.status()?.value)
                assertEquals("[]", response.content)
            }

            val f1 = addFile(createTempFile())
            val f2 = addFile(createTempFile())

            with(handleRequest(HttpMethod.Get, "/inputs/files")) {
                assertEquals(200, response.status()?.value)
                val content = response.content?.let { Json.decodeFromString<List<File>>(it) }
                assertEquals(2, content?.size)
                val files = content?.map { it.uri } ?: listOf()
                assertContains(files.toTypedArray(), f1?.uri)
                assertContains(files.toTypedArray(), f2?.uri)
            }
        }
    }

    @Test
    fun getFile() {
        withTestApplication({ module(testing = true) }) {
            with(handleRequest(HttpMethod.Get, "/inputs/files/someId")) {
                assertEquals(404, response.status()?.value)
            }

            val f = addFile(createTempFile())!!

            with(handleRequest(HttpMethod.Get, "/inputs/files/${f.id}")) {
                assertEquals(200, response.status()?.value)
                val content = response.content?.let { Json.decodeFromString<File>(it) }
                assertEquals(f.id, content?.id)
            }
        }
    }

    private fun createTempFile(): Path {
        val f = Files.createTempFile("inputtest", ".txt")
        f.toFile().deleteOnExit()
        return f
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

    private fun TestApplicationEngine.addFile(file: Path?): File? =
        postFile(file).response.content?.let { Json.decodeFromString<File>(it) }
}
