package ski.rss.socialworker

import ski.rss.functionalsupport.Result
import ski.rss.instagram.response.InstagramResponseService
import ski.rss.instagram.response.instagramPrefix
import ski.rss.workersupport.Worker

class InstagramWorker(
    override val name: String,
    private val responseService: InstagramResponseService
) : Worker<String> {
    override fun canExecute(task: String): Boolean = task.startsWith("$instagramPrefix:")
    override suspend fun execute(task: String): Result<Unit> = responseService.save(task.removePrefix("$instagramPrefix:"))
}
