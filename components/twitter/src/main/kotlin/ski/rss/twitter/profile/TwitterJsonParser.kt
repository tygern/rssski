package ski.rss.twitter.profile

import kotlinx.serialization.SerializationException
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import ski.rss.functionalsupport.Failure
import ski.rss.functionalsupport.Result
import ski.rss.functionalsupport.Success
import java.net.URI
import java.time.Instant

class TwitterJsonParser {
    companion object {
        private val logger: Logger = LoggerFactory.getLogger(TwitterJsonParser::class.java)
    }

    fun readProfile(json: String): Result<TwitterProfile> {
        val tweets = tweetsJson(json)
        val profile = profileJson(json)
        val media = mediaJson(json)

        return if (tweets == null || profile == null) {
            logger.error("""
                Failed to parse json response:
                
                {}
            """.trimIndent(), json)

            Failure("Failed to parse JSON from Twitter response.")
        } else {
            Success(twitterFeed(tweets, profile, media))
        }
    }

    private fun twitterFeed(tweets: JsonArray, profile: JsonObject, media: JsonArray): TwitterProfile =
        TwitterProfile(
            name = profile.getString("name"),
            username = "@${profile.getString("username")}",
            description = profile.getString("description"),
            link = URI("https://www.twitter.com/${profile.getString("username")}"),
            imageUrl = URI(profile.getString("profile_image_url")),
            tweets = tweets.map {
                twitterPost(it.jsonObject, profile, media)
            }
                .sortedByDescending(Tweet::tweetedAt),
        )

    private fun twitterPost(tweet: JsonObject, profile: JsonObject, media: JsonArray): Tweet =
        Tweet(
            description = tweet.getString("text"),
            mediaPreviewUrls = mediaPreviews(tweet, media),
            link = URI("https://www.twitter.com/${profile.getString("username")}/status/${tweet.getString("id")}"),
            tweetedAt = Instant.parse(tweet.getString("created_at"))
        )

    private fun mediaPreviews(tweet: JsonObject, media: JsonArray): List<URI> {
        val attachments = tweet["attachments"] ?: return listOf()

        return attachments.jsonObject.getArray("media_keys").mapNotNull {
            mediaUrl(it.jsonPrimitive.content, media)
        }
    }

    private fun mediaUrl(mediaKey: String, media: JsonArray): URI? {
        val mediaJson = media
            .map { it.jsonObject }
            .find { it.getString("media_key") == mediaKey }
            ?: return null

        val url = mediaJson["url"]
        val previewImageUrl = mediaJson["preview_image_url"]

        return when {
            url != null -> URI(url.jsonPrimitive.content)
            previewImageUrl != null -> URI(previewImageUrl.jsonPrimitive.content)
            else -> null
        }
    }

    private fun tweetsJson(json: String): JsonArray? = try {
        Json.decodeFromString<JsonObject>(json)["data"]?.jsonArray
    } catch (e: SerializationException) {
        logger.error("JSON parse error {}", e.message)
        null
    }

    private fun profileJson(json: String): JsonObject? = try {
        Json.decodeFromString<JsonObject>(json)["includes"]?.jsonObject?.get("users")?.jsonArray?.get(0)?.jsonObject
    } catch (e: SerializationException) {
        logger.error("JSON parse error {}", e.message)
        null
    }

    private fun mediaJson(json: String): JsonArray = try {
        Json.decodeFromString<JsonObject>(json)["includes"]?.jsonObject?.get("media")?.jsonArray ?: JsonArray(emptyList())
    } catch (e: SerializationException) {
        JsonArray(emptyList())
    }
}

private fun JsonObject.getArray(name: String): JsonArray = this[name]!!.jsonArray
private fun JsonObject.getString(name: String): String = this[name]!!.jsonPrimitive.content

data class TwitterProfile(
    val name: String,
    val username: String,
    val description: String,
    val link: URI,
    val imageUrl: URI,
    val tweets: List<Tweet>,
)

data class Tweet(
    val description: String,
    val mediaPreviewUrls: List<URI>,
    val link: URI,
    val tweetedAt: Instant,
)
