package ski.rss.instagram

import io.ktor.client.HttpClient
import io.ktor.client.features.ClientRequestException
import io.ktor.client.request.get
import java.net.URI

class InstagramClient(
    private val instagramUrl: URI,
    private val httpClient: HttpClient,
) {
    suspend fun fetchProfile(name: String): Result<String> = try {
        val response = httpClient.get<String>("$instagramUrl/$name?__a=1")
        Success(response)
    } catch (e: ClientRequestException) {
        Failure("Failed to fetch Instagram profile $name: ${e.message}")
    }
}
