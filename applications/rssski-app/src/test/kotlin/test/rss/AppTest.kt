package test.rss

import io.ktor.http.HttpMethod
import io.ktor.locations.KtorExperimentalLocationsAPI
import io.ktor.server.testing.handleRequest
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlin.test.Test
import kotlin.test.assertEquals

@KtorExperimentalLocationsAPI
class AppTest {
    @Test
    fun testEmptyHome() = testApp {
        handleRequest(HttpMethod.Get, "/").apply {
            assertEquals(200, response.status()?.value)

            val body = Json.decodeFromString<JsonObject>(response.content!!)
            assertEquals("rssski", body["application"]!!.jsonPrimitive.content)
        }
    }
}
