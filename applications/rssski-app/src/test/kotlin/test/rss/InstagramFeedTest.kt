package test.rss

import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.withCharset
import io.ktor.locations.KtorExperimentalLocationsAPI
import io.ktor.server.testing.contentType
import io.ktor.server.testing.handleRequest
import java.nio.charset.Charset
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

@KtorExperimentalLocationsAPI
class InstagramFeedTest {

    private val fakeInstagramServer = FakeInstagramServer(port = 8675)

    @BeforeTest
    fun setUp() = fakeInstagramServer.start()

    @AfterTest
    fun tearDown() = fakeInstagramServer.stop()

    @Test
    fun testFeed() = testApp {
        handleRequest(HttpMethod.Get, "/instagram/finnsadventures").apply {
            assertEquals(HttpStatusCode.OK, response.status())
            assertEquals(ContentType.Application.Rss.withCharset(Charset.defaultCharset()), response.contentType())

            assertEquals("""<rss version="2.0">
                    |<channel>
                        |<title>finnsadventures</title>
                        |<link>http://localhost:8675/finnsadventures</link>
                        |<description>Here is my biography</description>
                        |<image>
                            |<title>finnsadventures</title>
                            |<link>http://localhost:8675/finnsadventures</link>
                            |<url>http://example.com/hq_photo</url>
                        |</image>
                        |<item>
                            |<title>Asbury Park Convention Hall</title>
                            |<link>http://localhost:8675/p/Bx7b96cHeVs</link>
                            |<description><img src="https://instagram.example.com/display.jpg"/>
                            |
                            |Asbury Park Convention Hall description</description>
                            |<author>finnsadventures</author>
                            |<guid>http://localhost:8675/p/Bx7b96cHeVs</guid>
                        |</item>
                    |</channel>
                |</rss>
            """.trimMargin(), response.content!!)
        }
    }

    @Test
    fun testFeedError() = testApp {
        handleRequest(HttpMethod.Get, "/instagram/givemejunk").apply {
            assertEquals(HttpStatusCode.ServiceUnavailable, response.status())
            assertEquals(ContentType.Text.Plain.withCharset(Charset.defaultCharset()), response.contentType())

            assertEquals("Failed to parse JSON from Instagram response.".trimMargin(), response.content!!)
        }
    }
}
