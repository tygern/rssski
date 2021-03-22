package test.rss.socialworker

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.util.*
import kotlinx.coroutines.runBlocking
import ski.rss.functionalsupport.Success
import ski.rss.instagram.feed.InstagramAccount
import ski.rss.instagram.feed.InstagramClient
import ski.rss.instagram.feed.InstagramContentStorageService
import ski.rss.redissupport.JedisPoolProvider
import ski.rss.redissupport.StaticRedisUrlProvider
import ski.rss.socialaccount.SocialContentRepository
import ski.rss.socialworker.InstagramWorker
import ski.rss.twitter.feed.TwitterAccount
import test.rss.socialworker.support.FakeInstagramServer
import java.net.URI
import kotlin.test.*

@KtorExperimentalAPI
class InstagramWorkerTest {
    private val httpClient = HttpClient(CIO)
    private val jedisPool = JedisPoolProvider(StaticRedisUrlProvider(URI("redis://127.0.0.1:6379")))
    private val instagramServer = FakeInstagramServer(9521)

    private val instagramClient = InstagramClient(
        instagramUrl = instagramServer.url,
        httpClient = httpClient
    )
    private val contentRepository = SocialContentRepository(jedisPool)

    private val instagramResponseService = InstagramContentStorageService(
        instagramClient = instagramClient,
        contentRepository = contentRepository
    )

    private val worker = InstagramWorker(1, instagramResponseService)

    @BeforeTest
    fun setUp() {
        instagramServer.start()

        jedisPool.useResource {
            del("instagram:finnsadventures")
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

        val storedResponse = jedisPool.useResource {
            get("instagram:finnsadventures")
        }

        assertEquals("{\"some\": \"json\"}", storedResponse)
    }

    @Test
    fun canExecute() {
        assertTrue(worker.canExecute(InstagramAccount("finnsadventures")))
        assertFalse(worker.canExecute(TwitterAccount("finnsadventures")))
    }
}
