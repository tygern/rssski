package ski.rss.instagram.profile

import ski.rss.functionalsupport.Failure
import ski.rss.functionalsupport.Result
import ski.rss.instagram.response.InstagramResponseRepository

class InstagramProfileService(
    private val jsonParser: InstagramJsonParser,
    private val responseRepository: InstagramResponseRepository,
    private val profileRepository: InstagramProfileRepository,
) {
    fun fetch(name: String): Result<InstagramProfile> {
        val profileData = responseRepository.fetch(name)
            ?: return Failure("Feed not found for Instagram account $name.")

        return jsonParser.readProfile(profileData)
    }

    fun save(name: String) {
        profileRepository.save(name)
    }
}
