package test.rss.redissupport

import io.ktor.client.*
import io.ktor.client.engine.mock.*
import io.ktor.http.*
import io.ktor.http.HttpMethod.Companion.Get
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import ski.rss.functionalsupport.Failure
import ski.rss.functionalsupport.Success
import ski.rss.redissupport.ConfigServerRedisUrlProvider
import java.net.URI
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.fail

@ExperimentalCoroutinesApi
class ConfigServerRedisUrlProviderTest {
    @Test
    fun testFetchRedisUrl() = runBlockingTest {
        val provider = ConfigServerRedisUrlProvider(
            configUrl = URI("https://heroku.example.com/anapp/config-vars"),
            bearerToken = "some-token",
            httpClient = fakeHttpClient("""{"FORCE_HTTPS":"true",
                                            "REDIS_URL":"redis://u:p@ec2-86-75-309.compute-1.amazonaws.com:54321"}""".trimIndent()),
        )

        val result = provider.fetchUrl()

        require(result is Success)
        assertEquals(URI("redis://u:p@ec2-86-75-309.compute-1.amazonaws.com:54321"), result.value)
    }

    @Test
    fun testFetchRedisUrlBadUrl() = runBlockingTest {
        val provider = ConfigServerRedisUrlProvider(
            configUrl = URI("https://heroku.example.com/anapp/config-vars"),
            bearerToken = "some-token",
            httpClient = fakeHttpClient("""{"FORCE_HTTPS":"true",
                                            "REDIS_URL": " not-a-url "} """.trimIndent()),
        )

        val result = provider.fetchUrl()

        require(result is Failure)
        assertEquals("Failed to fetch REDIS_URL at https://heroku.example.com/anapp/config-vars", result.reason)
    }

    @Test
    fun testFetchRedisUrlNoRedisUrl() = runBlockingTest {
        val provider = ConfigServerRedisUrlProvider(
            configUrl = URI("https://heroku.example.com/anapp/config-vars"),
            bearerToken = "some-token",
            httpClient = fakeHttpClient("{\"FORCE_HTTPS\": true}"),
        )

        val result = provider.fetchUrl()

        require(result is Failure)
        assertEquals("Failed to fetch REDIS_URL at https://heroku.example.com/anapp/config-vars", result.reason)
    }

    @Test
    fun testFetchRedisUrlBadJson() = runBlockingTest {
        val provider = ConfigServerRedisUrlProvider(
            configUrl = URI("https://heroku.example.com/anapp/config-vars"),
            bearerToken = "some-token",
            httpClient = fakeHttpClient(""),
        )

        val result = provider.fetchUrl()

        require(result is Failure)
        assertEquals("Failed to fetch REDIS_URL at https://heroku.example.com/anapp/config-vars", result.reason)
    }
}

private fun Url.location() = "${protocol.name}://$host$fullPath"

private fun fakeHttpClient(response: String) = HttpClient(MockEngine) {
    engine {
        addHandler { request ->
            when (request.method to request.url.location()) {
                Get to "https://heroku.example.com/anapp/config-vars" -> {
                    if (request.headers["Authorization"] != "Bearer some-token") {
                        fail("Incorrect authorization")
                    }

                    if (request.headers["Accept"] != "application/vnd.heroku+json; version=3") {
                        fail("Incorrect accept header")
                    }

                    val responseHeaders = headersOf("Content-Type" to listOf(ContentType.Application.Json.toString()))
                    respond(response, headers = responseHeaders)
                }

                else -> error("Unhandled ${request.method} to ${request.url.location()}")
            }
        }
    }
}
