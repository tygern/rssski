package test.rss.socialaccount

import redis.clients.jedis.JedisPool
import ski.rss.socialaccount.SocialAccount
import ski.rss.socialaccount.SocialAccountRepository
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

private class ChirpAccount : SocialAccount(
    username = "fred",
    platform = "chirp",
)

class SocialAccountRepositoryTest {
    private val jedisPool = JedisPool()
    private val repo = SocialAccountRepository(jedisPool)

    private val account = ChirpAccount()

    @BeforeTest
    fun setUp() {
        jedisPool.resource.use {
            it.del("feeds")
        }
    }

    @Test
    fun testSave() {
        repo.save(account)

        val savedFeed = jedisPool.resource.use {
            it.smembers("feeds")
        }

        assertEquals(setOf("chirp:fred"), savedFeed)
    }
}
