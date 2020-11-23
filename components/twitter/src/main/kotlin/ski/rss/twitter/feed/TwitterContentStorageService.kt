package ski.rss.twitter.feed

import ski.rss.functionalsupport.Result
import ski.rss.socialaccount.SocialContentRepository

class TwitterContentStorageService(
    private val twitterClient: TwitterClient,
    private val contentRepository: SocialContentRepository
) {
    suspend fun save(account: TwitterAccount): Result<Unit> =
        twitterClient.fetchContent(account).map { content ->
            contentRepository.save(account, content)
        }
}
