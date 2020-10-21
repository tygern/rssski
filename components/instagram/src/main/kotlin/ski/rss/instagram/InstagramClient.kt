package ski.rss.instagram

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import java.net.URI

class InstagramClient(
    private val instagramUrl: URI,
    private val jsonParser: InstagramJsonParser,
    private val httpClient: HttpClient,
) {
    suspend fun fetchProfile(name: String): Result<InstagramProfile> {
        val profileUrl = "$instagramUrl/$name?__a=1"
        val profileData = httpClient.get<String>(profileUrl)

        return jsonParser.readProfile(profileData)
    }
}
