package ski.rss.instagram

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.net.URI

class InstagramClient(
    val instagramUrl: URI,
    val httpClient: HttpClient
) {
    suspend fun findFeed(name: String): Result<InstagramFeed> {
        val feedData = httpClient.get<String>("$instagramUrl/$name?__a=1")
        val json = Json.decodeFromString<JsonObject>(feedData)
            .getObject("graphql").getObject("user")

        return Result.Success(instagramFeed(name, json))
    }

    private fun instagramFeed(name: String, json: JsonObject): InstagramFeed {
        val posts = json.getObject("edge_owner_to_timeline_media")
            .getArray("edges")
            .take(10)
            .map(JsonElement::jsonObject)
            .map { it.getObject("node") }

        return InstagramFeed(
            name = name,
            description = json.getString("biography"),
            link = URI("$instagramUrl/$name"),
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
                                |<img src="${json.getString("display_url")}">
                                |$description
                            """.trimMargin(),
            link = URI("$instagramUrl/p/${json.getString("shortcode")}"),
        )
    }
}

fun JsonObject.getObject(name: String) = this[name]!!.jsonObject
fun JsonObject.getArray(name: String) = this[name]!!.jsonArray
fun JsonObject.getString(name: String) = this[name]!!.jsonPrimitive.content

data class InstagramFeed(
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

sealed class Result<T> {
    data class Success<T>(val value: T) : Result<T>()
    data class Failure<T>(val reason: String) : Result<T>()
}
