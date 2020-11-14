package ski.rss.instagram.response

import ski.rss.functionalsupport.Result

class InstagramResponseService(
    private val instagramClient: InstagramClient,
    private val responseRepository: InstagramResponseRepository,
) {
    suspend fun save(name: String): Result<Unit> =
        instagramClient.fetchProfile(name).map {
            responseRepository.save(name, it)
        }
}
