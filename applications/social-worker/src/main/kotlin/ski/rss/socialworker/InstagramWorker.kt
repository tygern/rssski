package ski.rss.socialworker

import ski.rss.functionalsupport.Failure
import ski.rss.functionalsupport.Result
import ski.rss.instagram.InstagramAccount
import ski.rss.instagram.response.InstagramResponseService
import ski.rss.socialaccount.SocialAccount
import ski.rss.workersupport.Worker

class InstagramWorker(
    override val numberOfThreads: Int,
    private val responseService: InstagramResponseService,
) : Worker<SocialAccount> {
    override val name = "Instagram"
    override fun canExecute(task: SocialAccount): Boolean = task is InstagramAccount
    override suspend fun execute(task: SocialAccount): Result<Unit> =
        if (task is InstagramAccount) {
            responseService.save(task.username)
        } else {
            Failure("Unable to execute $task")
        }
}
