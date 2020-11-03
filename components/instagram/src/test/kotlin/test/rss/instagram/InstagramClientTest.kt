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
import ski.rss.instagram.InstagramJsonParser
import ski.rss.instagram.InstagramProfile
import ski.rss.instagram.InstagramPost
import ski.rss.instagram.Result
import java.net.URI
import kotlin.test.assertEquals

@ExperimentalCoroutinesApi
class InstagramClientTest {
    private val client = InstagramClient(
        instagramUrl = URI("http://instagram.example.com"),
        jsonParser = InstagramJsonParser(URI("http://instagram.example.com")),
        httpClient = fakeHttpClient,
    )

    @Test
    fun testFetchProfile() = runBlockingTest {
        val result = client.fetchProfile("finnsadventures")

        require(result is Result.Success)
        assertEquals("finnsadventures", result.value.name)
    }

    @Test
    fun testJsonParseError() = runBlockingTest {
        val result = client.fetchProfile("givemejunk")

        require(result is Result.Failure)
        assertEquals("Failed to parse JSON from Instagram response.", result.reason)
    }
}

private fun Url.location() = "${protocol.name}://$host$fullPath"

private val fakeHttpClient = HttpClient(MockEngine) {
    engine {
        addHandler { request ->
            when (request.method to request.url.location()) {
                Get to "http://instagram.example.com/finnsadventures?__a=1" -> {
                    val responseHeaders = headersOf("Content-Type" to listOf(ContentType.Application.Json.toString()))
                    respond(javaClass.getResource("/finnsadventures.json").readText(), headers = responseHeaders)
                }
                Get to "http://instagram.example.com/givemejunk?__a=1" -> {
                    val responseHeaders = headersOf("Content-Type" to listOf(ContentType.Application.Xml.toString()))
                    respond("<junk>", headers = responseHeaders)
                }
                else -> error("Unhandled ${request.method} to ${request.url.location()}")
            }
        }
    }
}
