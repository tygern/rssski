package test.rss.instagram

import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod.Companion.Get
import io.ktor.http.Url
import io.ktor.http.fullPath
import io.ktor.http.headersOf
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Test
import ski.rss.instagram.InstagramClient
import java.net.URI
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

        assertEquals("a response", result)
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
                else -> error("Unhandled ${request.method} to ${request.url.location()}")
            }
        }
    }
}
