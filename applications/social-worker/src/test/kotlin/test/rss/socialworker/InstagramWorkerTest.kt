package test.rss.socialworker

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.util.KtorExperimentalAPI
import kotlinx.coroutines.runBlocking
import ski.rss.functionalsupport.Success
import ski.rss.instagram.response.InstagramClient
import ski.rss.instagram.response.InstagramResponseRepository
import ski.rss.instagram.response.InstagramResponseService
import ski.rss.instagram.response.instagramPrefix
import ski.rss.redissupport.jedisPool
import ski.rss.socialworker.InstagramAccount
import ski.rss.socialworker.InstagramWorker
import ski.rss.socialworker.TwitterAccount
import test.rss.socialworker.support.FakeInstagramServer
import java.net.URI
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@KtorExperimentalAPI
class InstagramWorkerTest {
    private val redisUrl = URI("redis://127.0.0.1:6379")
    private val httpClient = HttpClient(CIO)
    private val jedisPool = jedisPool(redisUrl)
    private val instagramServer = FakeInstagramServer(9521)

    private val instagramClient = InstagramClient(
        instagramUrl = instagramServer.url,
        httpClient = httpClient
    )
    private val instagramResponseRepository = InstagramResponseRepository(jedisPool)

    private val instagramResponseService = InstagramResponseService(
        instagramClient,
        instagramResponseRepository
    )

    private val worker = InstagramWorker(1, instagramResponseService)

    @BeforeTest
    fun setUp() {
        instagramServer.start()

        jedisPool.resource.use {
            it.del("$instagramPrefix:finnsadventures")
        }
    }

    @AfterTest
    fun tearDown() {
        instagramServer.stop()
    }

    @Test
    fun integrationTest() = runBlocking {
        val result = worker.execute(InstagramAccount("finnsadventures"))

        assert(result is Success)

        val storedResponse = jedisPool.resource.use {
            it.get("$instagramPrefix:finnsadventures")
        }

        assertEquals("{\"some\": \"json\"}", storedResponse)
    }

    @Test
    fun canExecute() {
        assertTrue(worker.canExecute(InstagramAccount("finnsadventures")))
        assertFalse(worker.canExecute(TwitterAccount("finnsadventures")))
    }
}
