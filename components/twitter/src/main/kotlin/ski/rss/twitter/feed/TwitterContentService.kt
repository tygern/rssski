package ski.rss.twitter.feed

import ski.rss.functionalsupport.Failure
import ski.rss.functionalsupport.Result
import ski.rss.socialaccount.SocialAccountRepository
import ski.rss.socialaccount.SocialContentRepository

class TwitterContentService(
    private val jsonParser: TwitterJsonParser,
    private val contentRepository: SocialContentRepository,
    private val accountRepository: SocialAccountRepository,
) {
    fun fetch(account: TwitterAccount): Result<TwitterContent> {
        val profileData = contentRepository.fetch(account)
            ?: return Failure("Feed not found for account $account.")

        return jsonParser.readProfile(profileData)
    }

    fun subscribe(account: TwitterAccount) {
        accountRepository.save(account)
    }
}
