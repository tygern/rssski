package ski.rss.instagram.feed

import ski.rss.functionalsupport.Failure
import ski.rss.functionalsupport.Result
import ski.rss.socialaccount.SocialAccountRepository
import ski.rss.socialaccount.SocialContentRepository

class InstagramContentService(
    private val jsonParser: InstagramJsonParser,
    private val contentRepository: SocialContentRepository,
    private val accountRepository: SocialAccountRepository,
) {
    fun fetch(account: InstagramAccount): Result<InstagramProfile> {
        val profileData = contentRepository.fetch(account)
            ?: return Failure("Feed not found for account $account.")

        return jsonParser.readProfile(profileData)
    }

    fun subscribe(account: InstagramAccount) {
        accountRepository.save(account)
    }
}
