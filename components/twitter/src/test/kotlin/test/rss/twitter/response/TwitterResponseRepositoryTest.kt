package test.rss.twitter.response

import redis.clients.jedis.JedisPool
import ski.rss.twitter.response.TwitterResponseRepository
import ski.rss.twitter.response.twitterPrefix
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class TwitterResponseRepositoryTest {
    private val jedisPool = JedisPool()
    private val repo = TwitterResponseRepository(jedisPool)

    @BeforeTest
    fun setUp() {
        jedisPool.resource.use {
            it.del("$twitterPrefix:fred")
        }
    }

    @Test
    fun testSave() {
        repo.save("fred", "{\"some\": \"json\"}")

        val savedValue = jedisPool.resource.use {
            it.get("$twitterPrefix:fred")
        }

        assertEquals("{\"some\": \"json\"}", savedValue)
    }

    @Test
    fun testFetch() {
        jedisPool.resource.use { it.set("$twitterPrefix:fred", "{\"a\": \"value\"}") }

        val result = repo.fetch("fred")

        assertEquals("{\"a\": \"value\"}", result)
    }

    @Test
    fun testFetchEmpty() {
        val result = repo.fetch("fred")

        assertEquals(null, result)
    }
}
