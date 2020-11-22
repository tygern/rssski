package ski.rss.twitter.feed

import ski.rss.functionalsupport.Result
import ski.rss.socialaccount.AccountContentRepository

class TwitterSaveContentService(
    private val twitterClient: TwitterClient,
    private val contentRepository: AccountContentRepository
) {
    suspend fun save(account: TwitterAccount): Result<Unit> =
        twitterClient.fetchContent(account).map { content ->
            contentRepository.save(account, content)
        }
}
