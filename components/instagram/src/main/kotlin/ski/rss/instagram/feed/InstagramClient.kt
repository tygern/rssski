package ski.rss.instagram.feed

import io.ktor.client.HttpClient
import io.ktor.client.features.ClientRequestException
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.readText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import ski.rss.functionalsupport.Failure
import ski.rss.functionalsupport.Result
import ski.rss.functionalsupport.Success
import java.net.URI

class InstagramClient(
    private val instagramUrl: URI,
    private val httpClient: HttpClient,
) {
    suspend fun fetchProfile(account: InstagramAccount): Result<String> = try {
        val response = httpClient.get<HttpResponse>("$instagramUrl/${account.username}?__a=1")
        val contentType = response.contentType()

        if (contentType != null && contentType.match(ContentType.Application.Json)) {
            Success(response.readText())
        } else {
            Failure("Failed to fetch account $account: Instagram did not return JSON, which probably means it want you to authenticate")
        }

    } catch (e: ClientRequestException) {
        Failure("Failed to fetch account $account: ${e.message}")
    }
}
