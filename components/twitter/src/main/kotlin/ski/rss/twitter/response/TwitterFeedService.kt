package ski.rss.twitter.response

import ski.rss.functionalsupport.Failure
import ski.rss.functionalsupport.Result

class TwitterFeedService(
    private val jsonParser: TwitterJsonParser,
    private val responseRepository: TwitterResponseRepository,
) {
    fun fetch(name: String): Result<TwitterProfile> {
        val profileData = responseRepository.fetch(name)
            ?: return Failure("Feed not found for Twitter account $name.")

        return jsonParser.readProfile(profileData)
    }
}
