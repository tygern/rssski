package test.rss

import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.locations.KtorExperimentalLocationsAPI
import io.ktor.server.testing.handleRequest
import io.ktor.util.KtorExperimentalAPI
import redis.clients.jedis.JedisPool
import ski.rss.twitter.response.twitterPrefix
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

@KtorExperimentalAPI
@KtorExperimentalLocationsAPI
class TwitterSaveProfileTest {
    private val jedisPool = JedisPool()

    @BeforeTest
    fun setUp() {
        jedisPool.resource.use {
            it.del("feeds")
        }
    }

    @Test
    fun testSaveProfile() = testApp {
        handleRequest(HttpMethod.Post, "/twitter/finnsadventures").apply {
            assertEquals(HttpStatusCode.NoContent, response.status())
        }

        val feeds = jedisPool.resource.use {
            it.smembers("feeds")
        }

        assertEquals(setOf("$twitterPrefix:finnsadventures"), feeds)
    }
}
