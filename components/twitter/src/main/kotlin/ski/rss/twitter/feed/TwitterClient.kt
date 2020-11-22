package ski.rss.twitter.feed

import io.ktor.client.HttpClient
import io.ktor.client.features.ClientRequestException
import io.ktor.client.request.headers
import io.ktor.client.request.request
import io.ktor.http.URLProtocol
import ski.rss.functionalsupport.Failure
import ski.rss.functionalsupport.Result
import ski.rss.functionalsupport.Success
import java.net.URI

class TwitterClient(
    private val twitterUrl: URI,
    private val bearerToken: String,
    private val httpClient: HttpClient,
) {
    suspend fun fetchContent(account: TwitterAccount): Result<String> = try {
        val response = httpClient.request<String> {
            url {
                protocol = URLProtocol.createOrDefault(twitterUrl.scheme)
                host = twitterUrl.host

                if (twitterUrl.portIsDefined()) {
                    port = twitterUrl.port
                }

                encodedPath = "/2/tweets/search/recent"

                parameters.apply {
                    append("query", "from:${account.username}")
                    append("max_results", "60")
                    append("expansions", "author_id,attachments.media_keys")
                    append("user.fields", "name,description,profile_image_url")
                    append("tweet.fields", "created_at,attachments")
                    append("media.fields", "preview_image_url,url")
                }
            }

            headers {
                append("Authorization", "Bearer $bearerToken")
            }
        }


        Success(response)
    } catch (e: ClientRequestException) {
        Failure("Failed to fetch content for account $account: ${e.message}")
    }
}

private fun URI.portIsDefined() = port != -1
