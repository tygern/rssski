package test.rss

import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.locations.KtorExperimentalLocationsAPI
import io.ktor.server.testing.handleRequest
import io.ktor.util.KtorExperimentalAPI
import org.junit.Before
import redis.clients.jedis.JedisPool
import kotlin.test.Test
import kotlin.test.assertEquals

@KtorExperimentalAPI
@KtorExperimentalLocationsAPI
class InstagramSaveProfileTest {
    private val jedisPool = JedisPool()

    @Before
    fun setUp() {
        jedisPool.resource.use {
            it.del("feeds")
        }
    }

    @Test
    fun testSaveProfile() = testApp {
        handleRequest(HttpMethod.Post, "/instagram/finnsadventures").apply {
            assertEquals(HttpStatusCode.NoContent, response.status())
        }

        val feeds = jedisPool.resource.use {
            it.smembers("feeds")
        }

        assertEquals(setOf("instagram:finnsadventures"), feeds)
    }
}
