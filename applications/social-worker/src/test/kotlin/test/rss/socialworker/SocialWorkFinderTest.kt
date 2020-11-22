package test.rss.socialworker

import ski.rss.instagram.feed.InstagramAccount
import ski.rss.redissupport.jedisPool
import ski.rss.socialworker.SocialWorkFinder
import ski.rss.twitter.feed.TwitterAccount
import java.net.URI
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class SocialWorkFinderTest {
    private val redisUrl = URI("redis://127.0.0.1:6379")
    private val jedisPool = jedisPool(redisUrl)

    private val workFinder = SocialWorkFinder(jedisPool)

    @BeforeTest
    fun setUp() {
        jedisPool.resource.use {
            it.del("feeds")
        }
    }

    @Test
    fun testFindRequested() {
        jedisPool.resource.use {
            it.sadd("feeds",
                "instagram:accidentallywesanderson",
                "instagram:chelseafc",
                "twitter:chelseafc",
                "another:account"
            )
        }

        val work = workFinder.findRequested()

        assertEquals(setOf(
            InstagramAccount("accidentallywesanderson"),
            InstagramAccount("chelseafc"),
            TwitterAccount("chelseafc")
        ), work.toSet())
    }

    @Test
    fun testFindRequestedEmptyFeeds() {
        val work = workFinder.findRequested()

        assertEquals(setOf(), work.toSet())
    }
}
