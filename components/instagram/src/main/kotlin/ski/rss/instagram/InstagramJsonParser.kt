package ski.rss.instagram

import kotlinx.serialization.SerializationException
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.net.URI

class InstagramJsonParser(
    private val instagramUrl: URI,
) {
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

            Result.Failure("Failed to parse JSON from Instagram response.")
        } else {
            Result.Success(instagramFeed(jsonObject))
        }
    }

    private fun instagramFeed(json: JsonObject): InstagramProfile {
        val posts = json.getObject("edge_owner_to_timeline_media")
            .getArray("edges")
            .take(10)
            .map(JsonElement::jsonObject)
            .map { it.getObject("node") }
        val username = json.getString("username")

        return InstagramProfile(
            name = username,
            description = json.getString("biography"),
            link = URI("$instagramUrl/$username"),
            imageUrl = URI(json.getString("profile_pic_url_hd")),
            posts = posts.map(this::instagramPost),
        )
    }

    private fun instagramPost(json: JsonObject): InstagramPost {
        val description = json.getObject("edge_media_to_caption")
            .getArray("edges").first().jsonObject
            .getObject("node").getString("text")

        return InstagramPost(
            title = json.getObject("location").getString("name"),
            description = """
                                |<img src="${json.getString("display_url")}"/>
                                |
                                |$description
                            """.trimMargin(),
            link = URI("$instagramUrl/p/${json.getString("shortcode")}"),
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
    val link: URI,
)
