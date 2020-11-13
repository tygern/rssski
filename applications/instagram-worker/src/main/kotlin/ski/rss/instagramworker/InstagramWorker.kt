package ski.rss.instagramworker

import ski.rss.functionalsupport.Result
import ski.rss.instagram.InstagramResponseCache
import ski.rss.workersupport.Worker

class InstagramWorker(
    override val name: String,
    private val instagramResponseCache: InstagramResponseCache
) : Worker<String> {
    override suspend fun execute(task: String): Result<Unit> = instagramResponseCache.store(task)
}
