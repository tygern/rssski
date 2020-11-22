package test.rss.twitter.feed

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
import ski.rss.twitter.feed.TwitterAccount
import ski.rss.twitter.feed.TwitterClient
import java.net.URI
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.fail

@ExperimentalCoroutinesApi
class TwitterClientTest {
    private val client = TwitterClient(
        twitterUrl = URI("http://twitter.example.com"),
        bearerToken = "some-token",
        httpClient = fakeHttpClient,
    )

    @Test
    fun testFetchProfile() = runBlockingTest {
        val result = client.fetchContent(TwitterAccount("finnsadventures"))

        require(result is Success)
        assertEquals("a response", result.value)
    }

    @Test
    fun testFetchProfileFailure() = runBlockingTest {
        val result = client.fetchContent(TwitterAccount("noprofilehere"))

        require(result is Failure)
        assertEquals("Failed to fetch content for account twitter:noprofilehere: Client request(http://twitter.example.com/2/tweets/search/recent?query=from%3Anoprofilehere&max_results=60&expansions=author_id%2Cattachments.media_keys&user.fields=name%2Cdescription%2Cprofile_image_url&tweet.fields=created_at%2Cattachments&media.fields=preview_image_url%2Curl) " +
            "invalid: 400 Bad Request", result.reason)
    }
}

private fun Url.location() = "${protocol.name}://$host$fullPath"

private val fakeHttpClient = HttpClient(MockEngine) {
    engine {
        addHandler { request ->
            when (request.method to request.url.location()) {
                Get to "http://twitter.example.com/2/tweets/search/recent?query=from%3Afinnsadventures&max_results=60&expansions=author_id%2Cattachments.media_keys&user.fields=name%2Cdescription%2Cprofile_image_url&tweet.fields=created_at%2Cattachments&media.fields=preview_image_url%2Curl" -> {
                    if (request.headers["Authorization"] != "Bearer some-token") {
                        fail("Incorrect authorization")
                    }

                    val responseHeaders = headersOf("Content-Type" to listOf(ContentType.Application.Json.toString()))
                    respond("a response", headers = responseHeaders)
                }
                Get to "http://twitter.example.com/2/tweets/search/recent?query=from%3Anoprofilehere&max_results=60&expansions=author_id%2Cattachments.media_keys&user.fields=name%2Cdescription%2Cprofile_image_url&tweet.fields=created_at%2Cattachments&media.fields=preview_image_url%2Curl" -> {
                    if (request.headers["Authorization"] != "Bearer some-token") {
                        fail("Incorrect authorization")
                    }

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
