package ski.rss.twitter.response

import ski.rss.functionalsupport.Result

class TwitterResponseService(
    private val twitterClient: TwitterClient,
    private val responseRepository: TwitterResponseRepository,
) {
    suspend fun save(name: String): Result<Unit> =
        twitterClient.fetchProfile(name).map {
            responseRepository.save(name, it)
        }
}
