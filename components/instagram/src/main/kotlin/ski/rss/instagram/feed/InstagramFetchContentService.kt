package ski.rss.instagram.feed

import ski.rss.functionalsupport.Failure
import ski.rss.functionalsupport.Result
import ski.rss.socialaccount.AccountContentRepository

class InstagramFetchContentService(
    private val jsonParser: InstagramJsonParser,
    private val contentRepository: AccountContentRepository,
) {
    fun fetch(account: InstagramAccount): Result<InstagramProfile> {
        val profileData = contentRepository.fetch(account)
            ?: return Failure("Feed not found for account $account.")

        return jsonParser.readProfile(profileData)
    }
}
