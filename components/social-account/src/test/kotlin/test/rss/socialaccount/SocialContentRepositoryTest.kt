package test.rss.socialaccount

import ski.rss.redissupport.JedisPoolProvider
import ski.rss.redissupport.StaticRedisUrlProvider
import ski.rss.socialaccount.SocialAccount
import ski.rss.socialaccount.SocialContentRepository
import java.net.URI
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

private class BirdAccount : SocialAccount(
    username = "fred",
    platform = "bird",
)

class SocialContentRepositoryTest {
    private val jedisPool = JedisPoolProvider(StaticRedisUrlProvider(URI("redis://127.0.0.1:6379")))
    private val repo = SocialContentRepository(jedisPool)

    private val account = BirdAccount()

    @BeforeTest
    fun setUp() {
        jedisPool.useResource { del("bird:fred") }
    }

    @Test
    fun testSave() {
        repo.save(account, "{\"some\": \"json\"}")

        val content = jedisPool.useResource { get("bird:fred") }

        assertEquals("{\"some\": \"json\"}", content)
    }

    @Test
    fun testFetch() {
        jedisPool.useResource { set("bird:fred", "{\"a\": \"value\"}") }

        val content = repo.fetch(account)

        assertEquals("{\"a\": \"value\"}", content)
    }

    @Test
    fun testFetchEmpty() {
        val content = repo.fetch(account)

        assertEquals(null, content)
    }
}
