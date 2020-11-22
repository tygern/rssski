package ski.rss.socialworker

import ski.rss.functionalsupport.Failure
import ski.rss.functionalsupport.Result
import ski.rss.socialaccount.SocialAccount
import ski.rss.twitter.feed.TwitterAccount
import ski.rss.twitter.feed.TwitterSaveContentService
import ski.rss.workersupport.Worker

class TwitterWorker(
    override val numberOfThreads: Int,
    private val saveContentService: TwitterSaveContentService,
) : Worker<SocialAccount> {
    override val name = "Twitter"
    override fun canExecute(task: SocialAccount): Boolean = task is TwitterAccount
    override suspend fun execute(task: SocialAccount): Result<Unit> =
        if (task is TwitterAccount) {
            saveContentService.save(task)
        } else {
            Failure("Unable to execute $task")
        }
}
