package ski.rss.instagram.response

import kotlinx.serialization.SerializationException
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.long
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import ski.rss.functionalsupport.Failure
import ski.rss.functionalsupport.Result
import ski.rss.functionalsupport.Success
import java.net.URI
import java.time.Instant

class InstagramJsonParser {
    companion object {
        private val logger: Logger = LoggerFactory.getLogger(InstagramJsonParser::class.java)
    }

    fun readProfile(json: String): Result<InstagramProfile> {
        val jsonObject = try {
            Json.decodeFromString<JsonObject>(json)["graphql"]?.jsonObject
                ?.get("user")?.jsonObject
        } catch (e: SerializationException) {
            logger.error("JSON parse error {}", e.message)
            null
        }

        return if (jsonObject == null) {
            logger.error("""
                Failed to parse json response:
                
                {}
            """.trimIndent(), json)

            Failure("Failed to parse JSON from Instagram response.")
        } else {
            Success(instagramFeed(jsonObject))
        }
    }

    private fun instagramFeed(json: JsonObject): InstagramProfile {
        val username = json.getString("username")
        val posts = json.getObject("edge_owner_to_timeline_media")
            .getArray("edges")
            .map(JsonElement::jsonObject)
            .map { it.getObject("node") }

        return InstagramProfile(
            name = username,
            description = json.getString("biography"),
            link = URI("https://www.instagram.com/$username"),
            imageUrl = URI(json.getString("profile_pic_url_hd")),
            posts = posts.map(this::instagramPost)
                .sortedByDescending(InstagramPost::takenAt),
        )
    }

    private fun instagramPost(json: JsonObject): InstagramPost {
        val description = json.getObject("edge_media_to_caption")
            .getArray("edges").first().jsonObject
            .getObject("node").getString("text")

        val title = if (json["location"] is JsonObject) {
            json.getObject("location").getString("name")
        } else {
            description.slice(0 until 40) + "…"
        }

        val takenAt = json["taken_at_timestamp"]!!.jsonPrimitive.long

        return InstagramPost(
            title = title,
            description = description,
            imageUrl = URI(json.getString("display_url")),
            link = URI("https://www.instagram.com/p/${json.getString("shortcode")}"),
            takenAt = Instant.ofEpochSecond(takenAt)
        )
    }
}

private fun JsonObject.getObject(name: String) = this[name]!!.jsonObject
private fun JsonObject.getArray(name: String) = this[name]!!.jsonArray
private fun JsonObject.getString(name: String) = this[name]!!.jsonPrimitive.content

data class InstagramProfile(
    val name: String,
    val description: String,
    val link: URI,
    val imageUrl: URI,
    val posts: List<InstagramPost>,
)

data class InstagramPost(
    val title: String,
    val description: String,
    val imageUrl: URI,
    val link: URI,
    val takenAt: Instant
)
