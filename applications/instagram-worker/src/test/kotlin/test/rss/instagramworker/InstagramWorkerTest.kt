package test.rss.instagramworker

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.util.KtorExperimentalAPI
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import ski.rss.functionalsupport.Success
import ski.rss.instagram.InstagramClient
import ski.rss.instagram.InstagramResponseCache
import ski.rss.instagram.InstagramResponseRepository
import ski.rss.instagramworker.InstagramWorker
import ski.rss.redissupport.jedisPool
import java.net.URI
import kotlin.test.assertEquals

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

    private val instagramResponseCache = InstagramResponseCache(
        instagramClient,
        instagramResponseRepository
    )

    @Before
    fun setUp() {
        instagramServer.start()

        jedisPool.resource.use {
            it.del("instagram:finnsadventures")
        }
    }

    @After
    fun tearDown() {
        instagramServer.stop()
    }

    @Test
    fun integrationTest() = runBlocking {
        val worker = InstagramWorker("test worker", instagramResponseCache)

        val result = worker.execute("finnsadventures")

        assert(result is Success)

        val storedResponse = jedisPool.resource.use {
            it.get("instagram:finnsadventures")
        }

        assertEquals("{\"some\": \"json\"}", storedResponse)
    }
}
