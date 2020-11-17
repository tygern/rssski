package ski.rss.socialworker

import ski.rss.functionalsupport.Result
import ski.rss.twitter.response.TwitterResponseService
import ski.rss.twitter.response.twitterPrefix
import ski.rss.workersupport.Worker

class TwitterWorker(
    override val name: String,
    private val responseService: TwitterResponseService
) : Worker<String> {
    override fun canExecute(task: String): Boolean = task.startsWith("$twitterPrefix:")
    override suspend fun execute(task: String): Result<Unit> = responseService.save(task.removePrefix("$twitterPrefix:"))
}
