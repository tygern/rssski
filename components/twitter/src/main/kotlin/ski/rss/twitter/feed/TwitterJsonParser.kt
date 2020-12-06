package ski.rss.twitter.feed

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
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

    private val formatter = Json { ignoreUnknownKeys = true }

    fun readProfile(json: String): Result<TwitterContent> {
        val twitterContent = try {
            formatter.decodeFromString<TwitterJson>(json)
        } catch (e: SerializationException) {
            return Failure("Failed to parse JSON from Twitter response.")
        }

        val profile = profileJson(json)
        val media = mediaJson(json)

        val tweets = twitterContent.data

        return if (profile == null) {
            logger.error(
                """
                Failed to parse json response:
                
                {}
            """.trimIndent(), json
            )

            Failure("Failed to parse JSON from Twitter response.")
        } else {
            Success(twitterFeed(tweets, profile, media))
        }
    }

    private fun twitterFeed(tweets: List<TweetJson>, profile: JsonObject, media: JsonArray): TwitterContent =
        TwitterContent(
            name = profile.getString("name"),
            username = "@${profile.getString("username")}",
            description = profile.getString("description"),
            link = URI("https://www.twitter.com/${profile.getString("username")}"),
            imageUrl = URI(profile.getString("profile_image_url")),
            tweets = tweets.map {
                twitterPost(it, profile, media)
            }
                .sortedByDescending(Tweet::tweetedAt),
        )

    private fun twitterPost(tweet: TweetJson, profile: JsonObject, media: JsonArray): Tweet =
        Tweet(
            description = tweet.text,
            mediaPreviewUrls = mediaPreviews(tweet, media),
            link = URI("https://www.twitter.com/${profile.getString("username")}/status/${tweet.id}"),
            tweetedAt = Instant.parse(tweet.createdAt)
        )

    private fun mediaPreviews(tweet: TweetJson, media: JsonArray): List<URI> {
        val mediaKeys = tweet.attachments.mediaKeys

        return mediaKeys.mapNotNull { mediaUrl(it, media) }
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

    private fun profileJson(json: String): JsonObject? = try {
        Json.decodeFromString<JsonObject>(json)["includes"]?.jsonObject?.get("users")?.jsonArray?.get(0)?.jsonObject
    } catch (e: SerializationException) {
        logger.error("JSON parse error {}", e.message)
        null
    }

    private fun mediaJson(json: String): JsonArray = try {
        Json.decodeFromString<JsonObject>(json)["includes"]?.jsonObject?.get("media")?.jsonArray
            ?: JsonArray(emptyList())
    } catch (e: SerializationException) {
        JsonArray(emptyList())
    }
}

private fun JsonObject.getString(name: String): String = this[name]!!.jsonPrimitive.content

@Serializable
private data class TwitterJson(
    val data: List<TweetJson>
)

@Serializable
private data class TweetJson(
    @SerialName("author_id")
    val authorId: String,
    val text: String,
    @SerialName("created_at")
    val createdAt: String,
    val id: String,
    val attachments: AttachmentsJson = AttachmentsJson(),
)

@Serializable
private data class AttachmentsJson(
    @SerialName("media_keys")
    val mediaKeys: List<String> = emptyList(),
)

data class TwitterContent(
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
