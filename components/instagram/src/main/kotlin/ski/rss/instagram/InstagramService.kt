package ski.rss.instagram

class InstagramService(
    private val jsonParser: InstagramJsonParser,
    private val instagramClient: InstagramClient,
) {
    suspend fun fetchProfile(name: String): Result<InstagramProfile> {
        val profileData = instagramClient.fetchProfile(name)

        return jsonParser.readProfile(profileData)
    }
}
