package ski.rss.instagram.response

import io.ktor.client.HttpClient
import io.ktor.client.features.ClientRequestException
import io.ktor.client.request.get
import ski.rss.functionalsupport.Failure
import ski.rss.functionalsupport.Result
import ski.rss.functionalsupport.Success
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
