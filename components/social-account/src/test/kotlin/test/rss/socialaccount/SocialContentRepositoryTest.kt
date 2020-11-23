package test.rss.socialaccount

import redis.clients.jedis.JedisPool
import ski.rss.socialaccount.SocialAccount
import ski.rss.socialaccount.SocialContentRepository
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

private class BirdAccount : SocialAccount(
    username = "fred",
    platform = "bird",
)

class SocialContentRepositoryTest {
    private val jedisPool = JedisPool()
    private val repo = SocialContentRepository(jedisPool)

    private val account = BirdAccount()

    @BeforeTest
    fun setUp() {
        jedisPool.resource.use {
            it.del("bird:fred")
        }
    }

    @Test
    fun testSave() {
        repo.save(account, "{\"some\": \"json\"}")

        val content = jedisPool.resource.use {
            it.get("bird:fred")
        }

        assertEquals("{\"some\": \"json\"}", content)
    }

    @Test
    fun testFetch() {
        jedisPool.resource.use { it.set("bird:fred", "{\"a\": \"value\"}") }

        val content = repo.fetch(account)

        assertEquals("{\"a\": \"value\"}", content)
    }

    @Test
    fun testFetchEmpty() {
        val content = repo.fetch(account)

        assertEquals(null, content)
    }
}
