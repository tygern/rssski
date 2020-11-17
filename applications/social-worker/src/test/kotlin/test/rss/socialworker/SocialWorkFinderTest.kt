package test.rss.socialworker

import ski.rss.instagram.response.instagramPrefix
import ski.rss.redissupport.jedisPool
import ski.rss.socialworker.InstagramAccount
import ski.rss.socialworker.SocialWorkFinder
import ski.rss.socialworker.TwitterAccount
import ski.rss.twitter.response.twitterPrefix
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
                "$instagramPrefix:accidentallywesanderson",
                "$instagramPrefix:chelseafc",
                "$twitterPrefix:chelseafc",
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
