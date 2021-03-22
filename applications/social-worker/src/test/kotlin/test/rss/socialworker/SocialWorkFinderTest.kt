package test.rss.socialworker

import ski.rss.instagram.feed.InstagramAccount
import ski.rss.redissupport.JedisPoolProvider
import ski.rss.redissupport.StaticRedisUrlProvider
import ski.rss.socialworker.SocialWorkFinder
import ski.rss.twitter.feed.TwitterAccount
import java.net.URI
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class SocialWorkFinderTest {
    private val jedisPool = JedisPoolProvider(StaticRedisUrlProvider(URI("redis://127.0.0.1:6379")))

    private val workFinder = SocialWorkFinder(jedisPool)

    @BeforeTest
    fun setUp() {
        jedisPool.useResource {
            del("feeds")
        }
    }

    @Test
    fun testFindRequested() {
        jedisPool.useResource {
            sadd(
                "feeds",
                "instagram:accidentallywesanderson",
                "instagram:chelseafc",
                "twitter:chelseafc",
                "another:account"
            )
        }

        val work = workFinder.findRequested()

        assertEquals(
            setOf(
                InstagramAccount("accidentallywesanderson"),
                InstagramAccount("chelseafc"),
                TwitterAccount("chelseafc")
            ), work.toSet()
        )
    }

    @Test
    fun testFindRequestedEmptyFeeds() {
        val work = workFinder.findRequested()

        assertEquals(listOf(), work)
    }
}
