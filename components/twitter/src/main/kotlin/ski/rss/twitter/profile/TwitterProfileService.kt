package ski.rss.twitter.profile

import ski.rss.functionalsupport.Failure
import ski.rss.functionalsupport.Result
import ski.rss.twitter.response.TwitterResponseRepository

class TwitterProfileService(
    private val jsonParser: TwitterJsonParser,
    private val responseRepository: TwitterResponseRepository,
    private val profileRepository: TwitterProfileRepository,
) {
    fun fetch(name: String): Result<TwitterProfile> {
        val profileData = responseRepository.fetch(name)
            ?: return Failure("Feed not found for Twitter account $name.")

        return jsonParser.readProfile(profileData)
    }

    fun save(name: String) {
        profileRepository.save(name)
    }
}
