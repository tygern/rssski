package test.rss

import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.withCharset
import io.ktor.locations.KtorExperimentalLocationsAPI
import io.ktor.server.testing.contentType
import io.ktor.server.testing.handleRequest
import io.ktor.util.KtorExperimentalAPI
import redis.clients.jedis.JedisPool
import java.nio.charset.Charset
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

@KtorExperimentalAPI
@KtorExperimentalLocationsAPI
class InstagramReadRssTest {
    private val jedisPool = JedisPool()

    @BeforeTest
    fun setUp() {
        jedisPool.resource.use {
            it.set("instagram:finnsadventures", javaClass.getResource("/instagram-finnsadventures.json").readText())
            it.set("instagram:givemejunk", "<junk>")
        }
    }

    @Test
    fun testFeed() = testApp {
        handleRequest(HttpMethod.Get, "/instagram/finnsadventures").apply {
            assertEquals(HttpStatusCode.OK, response.status())
            assertEquals(ContentType.Application.Rss.withCharset(Charset.defaultCharset()), response.contentType())

            assertEquals("""<?xml version = "1.0" encoding="utf-8"?>
                    |<rss version="2.0">
                    |<channel>
                        |<title>finnsadventures</title>
                        |<link>https://www.instagram.com/finnsadventures</link>
                        |<description>Here is my biography</description>
                        |<image>
                            |<title>finnsadventures</title>
                            |<link>https://www.instagram.com/finnsadventures</link>
                            |<url>http://example.com/hq_photo</url>
                        |</image>
                        |<item>
                            |<title>Asbury Park Convention Hall</title>
                            |<link>https://www.instagram.com/p/Bx7b96cHeVs</link>
                            |<description>
                                |<![CDATA[
                                |<img src="https://instagram.example.com/display.jpg"/><br>
                                |<br>
                                |Asbury Park Convention Hall description
                                |]]>
                            |</description>
                            |<author>finnsadventures</author>
                            |<guid>https://www.instagram.com/p/Bx7b96cHeVs</guid>
                            |<pubDate>Sun, 26 May 2019 14:14:08 GMT</pubDate>
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
