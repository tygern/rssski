package ski.rss.socialworker

import ski.rss.functionalsupport.Failure
import ski.rss.functionalsupport.Result
import ski.rss.twitter.response.TwitterResponseService
import ski.rss.workersupport.Worker

class TwitterWorker(
    override val name: String,
    private val responseService: TwitterResponseService,
) : Worker<SocialAccount> {
    override fun canExecute(task: SocialAccount): Boolean = task is TwitterAccount
    override suspend fun execute(task: SocialAccount): Result<Unit> =
        if (task is TwitterAccount) {
            responseService.save(task.name)
        } else {
            Failure("Unable to execute $task")
        }
}
