package ski.rss.instagram.feed

import ski.rss.functionalsupport.Result
import ski.rss.socialaccount.SocialContentRepository

class InstagramContentStorageService(
    private val instagramClient: InstagramClient,
    private val contentRepository: SocialContentRepository,
) {
    suspend fun storeContent(account: InstagramAccount): Result<Unit> =
        instagramClient.fetchProfile(account).map { content ->
            contentRepository.save(account, content)
        }
}
