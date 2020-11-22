package ski.rss.twitter.feed

import ski.rss.functionalsupport.Failure
import ski.rss.functionalsupport.Result
import ski.rss.socialaccount.AccountContentRepository

class TwitterFetchContentService(
    private val jsonParser: TwitterJsonParser,
    private val contentRepository: AccountContentRepository,
) {
    fun fetch(account: TwitterAccount): Result<TwitterContent> {
        val profileData = contentRepository.fetch(account)
            ?: return Failure("Feed not found for account $account.")

        return jsonParser.readProfile(profileData)
    }
}
