package test.rss.instagram

import org.junit.Before
import redis.clients.jedis.JedisPool
import ski.rss.instagram.InstagramResponseRepository
import kotlin.test.Test
import kotlin.test.assertEquals

class InstagramResponseRepositoryTest {

    private val jedisPool = JedisPool()
    private val repo = InstagramResponseRepository(jedisPool)

    @Before
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
    fun testSaveNullTakesNoAction() {
        repo.save("fred", "potato")
        repo.save("fred", null)

        val savedValue = jedisPool.resource.use {
            it.get("instagram:fred")
        }

        assertEquals("potato", savedValue)
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
