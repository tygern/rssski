package ski.rss.instagram

class InstagramResponseCache(
    private val instagramClient: InstagramClient,
    private val responseRepository: InstagramResponseRepository,
) {
    suspend fun store(name: String): Result<Unit> =
        instagramClient.fetchProfile(name).map {
            responseRepository.save(name, it)
        }
}
