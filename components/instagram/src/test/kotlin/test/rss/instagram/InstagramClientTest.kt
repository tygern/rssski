package test.rss.instagram

import kotlinx.coroutines.ExperimentalCoroutinesApi
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test
import ski.rss.instagram.InstagramClient
import kotlin.test.assertEquals

@ExperimentalCoroutinesApi
class InstagramClientTest {
    private lateinit var mockWebServer: MockWebServer
    private lateinit var client: InstagramClient


    @Before
    fun setUp() {
        mockWebServer = MockWebServer()
        mockWebServer.start()
        client = InstagramClient(
            instagramUrl = mockWebServer.url("").toUri(),
            httpClient = OkHttpClient(),
        )

    }

    @Test
    fun testFetchProfile() {
        val response = MockResponse()
            .setResponseCode(200)
            .setBody("a response")

        mockWebServer.enqueue(response)

        val result = client.fetchProfile("finnsadventures")

        assertEquals("a response", result)

        val request = mockWebServer.takeRequest()
        assertEquals("GET", request.method)
        assertEquals("/finnsadventures?__a=1", request.path)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }
}
