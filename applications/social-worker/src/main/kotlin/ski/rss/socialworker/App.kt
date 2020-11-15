package ski.rss.socialworker

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.util.KtorExperimentalAPI
import kotlinx.coroutines.runBlocking
import ski.rss.instagram.response.InstagramClient
import ski.rss.instagram.response.InstagramResponseRepository
import ski.rss.instagram.response.InstagramResponseService
import ski.rss.redissupport.jedisPool
import ski.rss.socialworker.instagram.InstagramWorkFinder
import ski.rss.socialworker.instagram.InstagramWorker
import ski.rss.workersupport.WorkScheduler
import java.net.URI
import kotlin.time.minutes

@KtorExperimentalAPI
fun main() = runBlocking {
    val instagramUrl = URI(System.getenv("INSTAGRAM_URL")
        ?: throw RuntimeException("Please set the INSTAGRAM_URL environment variable"))
    val redisUrl = System.getenv("REDIS_URL")?.let(::URI)
        ?: throw RuntimeException("Please set the REDIS_URL environment variable")
    val updateInterval = System.getenv("UPDATE_INTERVAL").toIntOrNull()
        ?: throw RuntimeException("Please set the UPDATE_INTERVAL environment variable to an integer value of minutes")

    val httpClient = HttpClient(CIO)
    val jedisPool = jedisPool(redisUrl)

    val instagramClient = InstagramClient(instagramUrl, httpClient)
    val instagramResponseRepository = InstagramResponseRepository(jedisPool)

    val instagramResponseService = InstagramResponseService(
        instagramClient,
        instagramResponseRepository
    )

    val scheduler = WorkScheduler(
        finder = InstagramWorkFinder(jedisPool),
        workers = listOf(
            InstagramWorker("1", instagramResponseService),
            InstagramWorker("2", instagramResponseService)
        ),
        interval = updateInterval.minutes,
    )

    scheduler.start()
}
