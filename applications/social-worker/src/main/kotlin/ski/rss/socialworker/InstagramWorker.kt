package ski.rss.socialworker

import ski.rss.functionalsupport.Failure
import ski.rss.functionalsupport.Result
import ski.rss.instagram.response.InstagramResponseService
import ski.rss.workersupport.Worker

class InstagramWorker(
    override val name: String,
    private val responseService: InstagramResponseService,
) : Worker<SocialAccount> {
    override fun canExecute(task: SocialAccount): Boolean = task is InstagramAccount
    override suspend fun execute(task: SocialAccount): Result<Unit> =
        if (task is InstagramAccount) {
            responseService.save(task.name)
        } else {
            Failure("Unable to execute $task")
        }
}
