package test.rss.instagramworker

import org.junit.Before
import org.junit.Test
import ski.rss.instagramworker.InstagramWorkFinder
import ski.rss.redissupport.jedisPool
import java.net.URI
import kotlin.test.assertEquals

class InstagramWorkFinderTest {
    private val redisUrl = URI("redis://127.0.0.1:6379")
    private val jedisPool = jedisPool(redisUrl)

    private val workFinder = InstagramWorkFinder(jedisPool)

    @Before
    fun setUp() {
        jedisPool.resource.use {
            it.del("feeds")
        }
    }

    @Test
    fun testFindRequested() {
        jedisPool.resource.use {
            it.sadd("feeds", "instagram:accidentallywesanderson", "instagram:chelseafc", "other:disregard")
        }

        val work = workFinder.findRequested()

        assertEquals(setOf("accidentallywesanderson", "chelseafc"), work.toSet())
    }

    @Test
    fun testFindRequestedEmptyFeeds() {
        val work = workFinder.findRequested()

        assertEquals(setOf(), work.toSet())
    }
}
