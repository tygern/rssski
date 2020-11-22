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
class TwitterReadRssTest {
    private val jedisPool = JedisPool()

    @BeforeTest
    fun setUp() {
        jedisPool.resource.use {
            it.set("twitter:chelseafc", javaClass.getResource("/twitter-chelseafc.json").readText())
            it.set("twitter:givemejunk", "<junk>")
        }
    }

    @Test
    fun testFeed() = testApp {
        handleRequest(HttpMethod.Get, "/twitter/chelseafc").apply {
            assertEquals(HttpStatusCode.OK, response.status())
            assertEquals(ContentType.Application.Rss.withCharset(Charset.defaultCharset()), response.contentType())

            assertEquals("""<?xml version = "1.0" encoding="utf-8"?>
                    |<rss version="2.0">
                    |<channel>
                        |<title>Chelsea FC</title>
                        |<link>https://www.twitter.com/ChelseaFC</link>
                        |<description>Welcome to the official Twitter account of Chelsea Football Club. Download our app, the 5th Stand! 📲</description>
                        |<image>
                            |<title>Chelsea FC</title>
                            |<link>https://www.twitter.com/ChelseaFC</link>
                            |<url>https://pbs.twimg.com/profile_images/1326853447044509697/9DtwRsdK_normal.jpg</url>
                        |</image>
                        |<item>
                            |<title>Chelsea FC (@ChelseaFC)</title>
                            |<link>https://www.twitter.com/ChelseaFC/status/1328822533005709318</link>
                            |<description>
                                |<![CDATA[
                                |Our Blues! 💙👏 Goals galore from across the planet today! https://t.co/aGz51KLVoK
                                |<br>
                                |<br><img src="https://pbs.twimg.com/media/EnDtCvwWEAEoAUM.jpg"/>
                                |<br><img src="https://pbs.twimg.com/media/EnDtCvtWMAgYPn7.jpg"/>
                                |]]>
                            |</description>
                            |<author>@ChelseaFC</author>
                            |<guid>https://www.twitter.com/ChelseaFC/status/1328822533005709318</guid>
                            |<pubDate>Tue, 17 Nov 2020 22:09:14 GMT</pubDate>
                        |</item>
                        |<item>
                            |<title>Chelsea FC (@ChelseaFC)</title>
                            |<link>https://www.twitter.com/ChelseaFC/status/1328821970931240963</link>
                            |<description>
                                |<![CDATA[
                                |Another @Calteck10 goal for @England&#x27;s #YoungLions! 🦁 https://t.co/fN5SKyJ0ir
                                |<br>
                                |<br><img src="https://pbs.twimg.com/media/EnDlyO8W8AcNllo.jpg"/>
                                |]]>
                            |</description>
                            |<author>@ChelseaFC</author>
                            |<guid>https://www.twitter.com/ChelseaFC/status/1328821970931240963</guid>
                            |<pubDate>Tue, 17 Nov 2020 22:07:00 GMT</pubDate>
                        |</item>
                        |<item>
                            |<title>Chelsea FC (@ChelseaFC)</title>
                            |<link>https://www.twitter.com/ChelseaFC/status/1328754629262385159</link>
                            |<description>
                                |<![CDATA[
                                |Still plenty to play for on the international scene! 🌎
                                |<br>
                                |
                                |]]>
                            |</description>
                            |<author>@ChelseaFC</author>
                            |<guid>https://www.twitter.com/ChelseaFC/status/1328754629262385159</guid>
                            |<pubDate>Tue, 17 Nov 2020 17:39:25 GMT</pubDate>
                        |</item>
                    |</channel>
                |</rss>
            """.trimMargin(), response.content!!)
        }
    }

    @Test
    fun testFeedError() = testApp {
        handleRequest(HttpMethod.Get, "/twitter/givemejunk").apply {
            assertEquals(HttpStatusCode.ServiceUnavailable, response.status())
            assertEquals(ContentType.Text.Plain.withCharset(Charset.defaultCharset()), response.contentType())

            assertEquals("Failed to parse JSON from Twitter response.".trimMargin(), response.content!!)
        }
    }
}
