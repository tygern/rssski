package ski.rss.instagram

class InstagramResponseCache(
    private val instagramClient: InstagramClient,
    private val responseRepository: InstagramResponseRepository,
) {
    suspend fun store(name: String) {
        val profileData = instagramClient.fetchProfile(name)

        responseRepository.save(name, profileData)
    }
}
