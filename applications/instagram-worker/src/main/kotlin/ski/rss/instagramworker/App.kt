package ski.rss.instagramworker

import kotlinx.coroutines.runBlocking
import ski.rss.workersupport.WorkScheduler
import kotlin.time.hours

fun main() = runBlocking {

    val scheduler = WorkScheduler(
        finder = InstagramWorkFinder(),
        workers = listOf(InstagramWorker("1"), InstagramWorker("2")),
        interval = 1.hours
    )

    scheduler.start()
}
