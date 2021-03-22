package test.rss.socialworker

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.util.*
import kotlinx.coroutines.runBlocking
import ski.rss.functionalsupport.Failure
import ski.rss.instagram.feed.InstagramAccount
import ski.rss.redissupport.JedisPoolProvider
import ski.rss.redissupport.StaticRedisUrlProvider
import ski.rss.socialaccount.SocialContentRepository
import ski.rss.socialworker.TwitterWorker
import ski.rss.twitter.feed.TwitterAccount
import ski.rss.twitter.feed.TwitterClient
import ski.rss.twitter.feed.TwitterContentStorageService
import test.rss.socialworker.support.FakeTwitterServer
import java.net.URI
import kotlin.test.*

@KtorExperimentalAPI
class TwitterWorkerTest {
    private val httpClient = HttpClient(CIO)
    private val jedisPool = JedisPoolProvider(StaticRedisUrlProvider(URI("redis://127.0.0.1:6379")))
    private val twitterServer = FakeTwitterServer(9632)

    private val twitterClient = TwitterClient(
        twitterUrl = twitterServer.url,
        bearerToken = twitterServer.validBearerToken,
        httpClient = httpClient,
    )
    private val contentRepository = SocialContentRepository(jedisPool)

    private val twitterResponseService = TwitterContentStorageService(
        twitterClient,
        contentRepository
    )

    private val worker = TwitterWorker(1, twitterResponseService)

    @BeforeTest
    fun setUp() {
        twitterServer.start()

        jedisPool.useResource { del("twitter:chelseafc") }
    }

    @AfterTest
    fun tearDown() {
        twitterServer.stop()
    }

    @Test
    fun integrationTest() = runBlocking {

        val result = worker.execute(TwitterAccount("chelseafc"))

        if (result is Failure) {
            fail(result.reason)
        } else {
            val storedResponse = jedisPool.useResource { get("twitter:chelseafc") }

            assertEquals("{\"some\": \"json\"}", storedResponse)
        }
    }

    @Test
    fun canExecute() {
        assertTrue(worker.canExecute(TwitterAccount("finnsadventures")))
        assertFalse(worker.canExecute(InstagramAccount("finnsadventures")))
    }
}
