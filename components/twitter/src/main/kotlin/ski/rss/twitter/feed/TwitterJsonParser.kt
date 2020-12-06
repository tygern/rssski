package ski.rss.twitter.feed

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import ski.rss.functionalsupport.Failure
import ski.rss.functionalsupport.Result
import ski.rss.functionalsupport.Success
import java.net.URI
import java.time.Instant

class TwitterJsonParser {
    private val formatter = Json { ignoreUnknownKeys = true }

    fun readProfile(json: String): Result<TwitterContent> {
        val twitterContent = try {
            formatter.decodeFromString<TwitterJson>(json)
        } catch (e: SerializationException) {
            return Failure("Failed to parse JSON from Twitter response.")
        }

        val tweets = twitterContent.data
        val media = twitterContent.includes.media
        val profile = twitterContent.includes.users[0]

        return Success(twitterContent(tweets, profile, media))
    }

    private fun twitterContent(tweets: List<TweetJson>, profile: UserJson, media: List<MediaJson>): TwitterContent =
        TwitterContent(
            name = profile.name,
            username = "@${profile.username}",
            description = profile.description,
            link = URI("https://www.twitter.com/${profile.username}"),
            imageUrl = URI(profile.profileImageUrl),
            tweets = tweets.map {
                twitterPost(it, profile, media)
            }
                .sortedByDescending(Tweet::tweetedAt),
        )

    private fun twitterPost(tweet: TweetJson, profile: UserJson, media: List<MediaJson>): Tweet =
        Tweet(
            description = tweet.text,
            mediaPreviewUrls = mediaPreviews(tweet, media),
            link = URI("https://www.twitter.com/${profile.username}/status/${tweet.id}"),
            tweetedAt = Instant.parse(tweet.createdAt)
        )

    private fun mediaPreviews(tweet: TweetJson, media: List<MediaJson>): List<URI> {
        val mediaKeys = tweet.attachments.mediaKeys

        return mediaKeys.mapNotNull { mediaUrl(it, media) }
    }

    private fun mediaUrl(mediaKey: String, media: List<MediaJson>): URI? {
        val mediaJson = media
            .find { it.mediaKey == mediaKey }
            ?: return null

        return when {
            mediaJson.url != null -> URI(mediaJson.url)
            mediaJson.previewImageUrl != null -> URI(mediaJson.previewImageUrl)
            else -> null
        }
    }
}

@Serializable
private data class TwitterJson(
    val data: List<TweetJson> = emptyList(),
    val includes: IncludesJson
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

@Serializable
private data class IncludesJson(
    val media: List<MediaJson> = emptyList(),
    val users: List<UserJson>,
)

@Serializable
private data class MediaJson(
    @SerialName("media_key")
    val mediaKey: String,
    val url: String? = null,
    @SerialName("preview_image_url")
    val previewImageUrl: String? = null,
)

@Serializable
private data class UserJson(
    val username: String,
    val name: String,
    val description: String,
    val id: String,
    @SerialName("profile_image_url")
    val profileImageUrl: String,
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
