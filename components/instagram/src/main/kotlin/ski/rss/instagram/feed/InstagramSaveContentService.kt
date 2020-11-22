package ski.rss.instagram.feed

import ski.rss.functionalsupport.Result
import ski.rss.socialaccount.AccountContentRepository

class InstagramSaveContentService(
    private val instagramClient: InstagramClient,
    private val contentRepository: AccountContentRepository,
) {
    suspend fun save(account: InstagramAccount): Result<Unit> =
        instagramClient.fetchProfile(account).map { content ->
            contentRepository.save(account, content)
        }
}
