package test.rss.instagram.profile

import redis.clients.jedis.JedisPool
import ski.rss.instagram.profile.InstagramProfileRepository
import ski.rss.instagram.response.instagramPrefix
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class InstagramProfileRepositoryTest {
    private val jedisPool = JedisPool()
    private val repo = InstagramProfileRepository(jedisPool)

    @BeforeTest
    fun setUp() {
        jedisPool.resource.use {
            it.del("feeds")
        }
    }

    @Test
    fun testSave() {
        repo.save("accidentallywesanderson")

        val savedFeed = jedisPool.resource.use {
            it.smembers("feeds")
        }

        assertEquals(setOf("$instagramPrefix:accidentallywesanderson"), savedFeed)
    }
}
