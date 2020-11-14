package test.rss.instagram.response

import redis.clients.jedis.JedisPool
import ski.rss.instagram.response.InstagramResponseRepository
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class InstagramResponseRepositoryTest {
    private val jedisPool = JedisPool()
    private val repo = InstagramResponseRepository(jedisPool)

    @BeforeTest
    fun setUp() {
        jedisPool.resource.use {
            it.del("instagram:fred")
        }
    }

    @Test
    fun testSave() {
        repo.save("fred", "{\"some\": \"json\"}")

        val savedValue = jedisPool.resource.use {
            it.get("instagram:fred")
        }

        assertEquals("{\"some\": \"json\"}", savedValue)
    }

    @Test
    fun testFetch() {
        jedisPool.resource.use { it.set("instagram:fred", "{\"a\": \"value\"}") }

        val result = repo.fetch("fred")

        assertEquals("{\"a\": \"value\"}", result)
    }

    @Test
    fun testFetchEmpty() {
        val result = repo.fetch("fred")

        assertEquals(null, result)
    }
}