package ski.rss.socialworker.instagram

import ski.rss.functionalsupport.Result
import ski.rss.instagram.response.InstagramResponseService
import ski.rss.workersupport.Worker

class InstagramWorker(
    override val name: String,
    private val responseService: InstagramResponseService
) : Worker<String> {
    override suspend fun execute(task: String): Result<Unit> = responseService.save(task)
}
