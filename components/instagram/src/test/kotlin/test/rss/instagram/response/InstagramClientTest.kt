package test.rss.instagram.response

import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod.Companion.Get
import io.ktor.http.HttpStatusCode
import io.ktor.http.Url
import io.ktor.http.fullPath
import io.ktor.http.headersOf
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import ski.rss.functionalsupport.Failure
import ski.rss.functionalsupport.Success
import ski.rss.instagram.response.InstagramClient
import java.net.URI
import kotlin.test.Test
import kotlin.test.assertEquals

@ExperimentalCoroutinesApi
class InstagramClientTest {
    private val client = InstagramClient(
        instagramUrl = URI("http://instagram.example.com"),
        httpClient = fakeHttpClient,
    )

    @Test
    fun testFetchProfile() = runBlockingTest {
        val result = client.fetchProfile("finnsadventures")

        require(result is Success)
        assertEquals("a response", result.value)
    }

    @Test
    fun testFetchProfileFailure() = runBlockingTest {
        val result = client.fetchProfile("noprofilehere")

        require(result is Failure)
        assertEquals("Failed to fetch Instagram profile noprofilehere: Client request(http://instagram.example.com/noprofilehere?__a=1) invalid: 400 Bad Request", result.reason)
    }
}

private fun Url.location() = "${protocol.name}://$host$fullPath"

private val fakeHttpClient = HttpClient(MockEngine) {
    engine {
        addHandler { request ->
            when (request.method to request.url.location()) {
                Get to "http://instagram.example.com/finnsadventures?__a=1" -> {
                    val responseHeaders = headersOf("Content-Type" to listOf(ContentType.Application.Json.toString()))
                    respond("a response", headers = responseHeaders)
                }
                Get to "http://instagram.example.com/noprofilehere?__a=1" -> {
                    val responseHeaders = headersOf("Content-Type" to listOf(ContentType.Application.Json.toString()))
                    respond(
                        content = "an error message",
                        headers = responseHeaders,
                        status = HttpStatusCode.BadRequest
                    )
                }
                else -> error("Unhandled ${request.method} to ${request.url.location()}")
            }
        }
    }
}
