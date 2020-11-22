package ski.rss.socialworker

import ski.rss.functionalsupport.Failure
import ski.rss.functionalsupport.Result
import ski.rss.instagram.feed.InstagramAccount
import ski.rss.instagram.feed.InstagramSaveContentService
import ski.rss.socialaccount.SocialAccount
import ski.rss.workersupport.Worker

class InstagramWorker(
    override val numberOfThreads: Int,
    private val saveContentService: InstagramSaveContentService,
) : Worker<SocialAccount> {
    override val name = "Instagram"
    override fun canExecute(task: SocialAccount): Boolean = task is InstagramAccount
    override suspend fun execute(task: SocialAccount): Result<Unit> =
        if (task is InstagramAccount) {
            saveContentService.save(task)
        } else {
            Failure("Unable to execute $task")
        }
}
