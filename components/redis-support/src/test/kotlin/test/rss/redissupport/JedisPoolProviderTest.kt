package test.rss.redissupport

import org.junit.Before
import ski.rss.redissupport.JedisPoolProvider
import ski.rss.redissupport.StaticRedisUrlProvider
import java.net.URI
import kotlin.test.Test
import kotlin.test.assertEquals

class JedisPoolProviderTest {
    private val jedisPool = JedisPoolProvider(StaticRedisUrlProvider(URI("redis://127.0.0.1:6379")))

    @Before
    fun setUp() {
        jedisPool.useResource {
            del("hello")
        }
    }

    @Test
    fun testUseResource() {
        jedisPool.useResource {
            set("hello", "world")
        }

        val result = jedisPool.useResource { get("hello") }

        assertEquals("world", result)
    }
}
