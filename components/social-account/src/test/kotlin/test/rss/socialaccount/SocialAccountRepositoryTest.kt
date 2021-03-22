package test.rss.socialaccount

import ski.rss.redissupport.JedisPoolProvider
import ski.rss.redissupport.StaticRedisUrlProvider
import ski.rss.socialaccount.SocialAccount
import ski.rss.socialaccount.SocialAccountRepository
import java.net.URI
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

private class ChirpAccount : SocialAccount(
    username = "fred",
    platform = "chirp",
)

class SocialAccountRepositoryTest {
    private val jedisPool = JedisPoolProvider(StaticRedisUrlProvider(URI("redis://127.0.0.1:6379")))
    private val repo = SocialAccountRepository(jedisPool)

    private val account = ChirpAccount()

    @BeforeTest
    fun setUp() {
        jedisPool.useResource { del("feeds") }
    }

    @Test
    fun testSave() {
        repo.save(account)

        val savedFeed = jedisPool.useResource { smembers("feeds") }

        assertEquals(setOf("chirp:fred"), savedFeed)
    }
}
