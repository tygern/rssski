package ski.rss.instagramworker

import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import redis.clients.jedis.JedisPool
import ski.rss.instagram.InstagramClient
import ski.rss.instagram.InstagramResponseCache
import ski.rss.instagram.InstagramResponseRepository
import ski.rss.workersupport.WorkScheduler
import java.net.URI
import kotlin.time.hours

fun main() = runBlocking {
    val instagramUrl = URI(System.getenv("INSTAGRAM_URL") ?: throw RuntimeException("Please set the INSTAGRAM_URL environment variable"))
    val httpClient = OkHttpClient()
    val jedisPool = JedisPool()

    val instagramClient = InstagramClient(instagramUrl, httpClient)
    val instagramResponseRepository = InstagramResponseRepository(jedisPool)

    val instagramResponseCache = InstagramResponseCache(
        instagramClient,
        instagramResponseRepository
    )

    val scheduler = WorkScheduler(
        finder = InstagramWorkFinder(),
        workers = listOf(
            InstagramWorker("1", instagramResponseCache),
            InstagramWorker("2", instagramResponseCache)
        ),
        interval = 1.hours
    )

    scheduler.start()
}
