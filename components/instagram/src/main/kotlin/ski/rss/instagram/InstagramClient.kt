package ski.rss.instagram

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import java.net.URI

class InstagramClient(
    private val instagramUrl: URI,
    private val httpClient: HttpClient,
) {
    suspend fun fetchProfile(name: String): String = httpClient.get("$instagramUrl/$name?__a=1")
}
