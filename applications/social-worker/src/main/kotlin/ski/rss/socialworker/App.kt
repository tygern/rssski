package ski.rss.socialworker

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.util.*
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.runBlocking
import ski.rss.instagram.feed.InstagramClient
import ski.rss.instagram.feed.InstagramContentStorageService
import ski.rss.redissupport.ConfigServerRedisUrlProvider
import ski.rss.redissupport.JedisPoolProvider
import ski.rss.socialaccount.SocialContentRepository
import ski.rss.twitter.feed.TwitterClient
import ski.rss.twitter.feed.TwitterContentStorageService
import ski.rss.workersupport.WorkScheduler
import java.net.URI
import kotlin.time.minutes
import kotlin.time.seconds

@ObsoleteCoroutinesApi
@KtorExperimentalAPI
fun main() = runBlocking {
    val instagramUrl = URI(System.getenv("INSTAGRAM_URL")
        ?: throw RuntimeException("Please set the INSTAGRAM_URL environment variable"))
    val twitterUrl = URI(System.getenv("TWITTER_URL")
        ?: throw RuntimeException("Please set the TWITTER_URL environment variable"))
    val twitterBearerToken = System.getenv("TWITTER_BEARER_TOKEN")
        ?: throw RuntimeException("Please set the TWITTER_BEARER_TOKEN environment variable")
    val updateInterval = System.getenv("UPDATE_INTERVAL").toIntOrNull()
        ?: throw RuntimeException("Please set the UPDATE_INTERVAL environment variable to an integer value of minutes")
    val configBearerToken = System.getenv("CONFIG_BEARER_TOKEN")
        ?: throw RuntimeException("Please set the CONFIG_BEARER_TOKEN environment variable")
    val configUrl = URI(System.getenv("CONFIG_URL")
        ?: throw RuntimeException("Please set the CONFIG_URL environment variable"))

    val httpClient = HttpClient(CIO)

    val redisUrlProvider = ConfigServerRedisUrlProvider(
        configUrl = configUrl,
        bearerToken = configBearerToken,
        httpClient = httpClient,
    )
    val jedisPool = JedisPoolProvider(redisUrlProvider).apply {
        updateEvery(10.minutes)
    }

    val contentRepository = SocialContentRepository(jedisPool)

    val instagramContentStorageService = InstagramContentStorageService(
        instagramClient = InstagramClient(instagramUrl, httpClient),
        contentRepository = contentRepository
    )

    val twitterContentStorageService = TwitterContentStorageService(
        twitterClient = TwitterClient(twitterUrl, twitterBearerToken, httpClient),
        contentRepository = contentRepository
    )

    val scheduler = WorkScheduler(
        finder = SocialWorkFinder(jedisPool),
        workers = listOf(
            InstagramWorker(numberOfThreads = 1, instagramContentStorageService),
            TwitterWorker(numberOfThreads = 2, twitterContentStorageService),
        ),
        interval = updateInterval.minutes,
    )

    scheduler.start()
}
