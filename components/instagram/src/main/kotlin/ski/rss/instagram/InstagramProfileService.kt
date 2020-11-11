package ski.rss.instagram

class InstagramProfileService(
    private val jsonParser: InstagramJsonParser,
    private val responseRepository: InstagramResponseRepository,
) {
    fun fetch(name: String): Result<InstagramProfile> {
        val profileData = responseRepository.fetch(name)
            ?: return Failure("Feed not found for Instagram account $name.")

        return jsonParser.readProfile(profileData)
    }
}
