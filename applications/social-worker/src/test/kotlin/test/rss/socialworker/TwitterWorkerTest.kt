package test.rss.socialworker

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.util.KtorExperimentalAPI
import kotlinx.coroutines.runBlocking
import ski.rss.functionalsupport.Failure
import ski.rss.instagram.InstagramAccount
import ski.rss.redissupport.jedisPool
import ski.rss.socialworker.TwitterWorker
import ski.rss.twitter.TwitterAccount
import ski.rss.twitter.response.TwitterClient
import ski.rss.twitter.response.TwitterResponseRepository
import ski.rss.twitter.response.TwitterResponseService
import ski.rss.twitter.response.twitterPrefix
import test.rss.socialworker.support.FakeTwitterServer
import java.net.URI
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.test.fail

@KtorExperimentalAPI
class TwitterWorkerTest {
    private val redisUrl = URI("redis://127.0.0.1:6379")
    private val httpClient = HttpClient(CIO)
    private val jedisPool = jedisPool(redisUrl)
    private val twitterServer = FakeTwitterServer(9632)

    private val twitterClient = TwitterClient(
        twitterUrl = twitterServer.url,
        bearerToken = twitterServer.validBearerToken,
        httpClient = httpClient,
    )
    private val twitterResponseRepository = TwitterResponseRepository(jedisPool)

    private val twitterResponseService = TwitterResponseService(
        twitterClient,
        twitterResponseRepository
    )

    private val worker = TwitterWorker(1, twitterResponseService)

    @BeforeTest
    fun setUp() {
        twitterServer.start()

        jedisPool.resource.use {
            it.del("$twitterPrefix:chelseafc")
        }
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
            val storedResponse = jedisPool.resource.use {
                it.get("$twitterPrefix:chelseafc")
            }

            assertEquals("{\"some\": \"json\"}", storedResponse)
        }
    }

    @Test
    fun canExecute() {
        assertTrue(worker.canExecute(TwitterAccount("finnsadventures")))
        assertFalse(worker.canExecute(InstagramAccount("finnsadventures")))
    }
}
