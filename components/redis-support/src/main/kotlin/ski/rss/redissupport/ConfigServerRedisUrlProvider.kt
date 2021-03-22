package ski.rss.redissupport

import io.ktor.client.*
import io.ktor.client.features.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.SerializationException
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonPrimitive
import ski.rss.functionalsupport.Failure
import ski.rss.functionalsupport.Result
import ski.rss.functionalsupport.Success
import java.net.URI
import java.net.URISyntaxException

class ConfigServerRedisUrlProvider(
    private val configUrl: URI,
    private val bearerToken: String,
    private val httpClient: HttpClient,
) : RedisUrlProvider {
    override fun fetchUrl(): Result<URI> {
        return try {
            val response = runBlocking {
                httpClient.request<String> {
                    url {
                        protocol = URLProtocol.createOrDefault(configUrl.scheme)
                        host = configUrl.host

                        if (configUrl.portIsDefined()) {
                            port = configUrl.port
                        }

                        encodedPath = configUrl.path
                    }

                    headers {
                        append("Authorization", "Bearer $bearerToken")
                        append("Accept", "application/vnd.heroku+json; version=3")
                    }
                }
            }

            Json.decodeFromString<JsonObject>(response)["REDIS_URL"]?.jsonPrimitive?.content?.let { redisUrl ->
                Success(URI(redisUrl))
            } ?: Failure("Failed to fetch REDIS_URL at $configUrl")
        } catch (e: ClientRequestException) {
            Failure("Failed to connect to $configUrl")
        } catch (e: URISyntaxException) {
            Failure("Failed to fetch REDIS_URL at $configUrl")
        } catch (e: SerializationException) {
            Failure("Failed to fetch REDIS_URL at $configUrl")
        }
    }
}

private fun URI.portIsDefined() = port != -1
