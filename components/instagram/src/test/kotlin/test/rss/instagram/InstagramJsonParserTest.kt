package test.rss.instagram

import ski.rss.instagram.InstagramJsonParser
import ski.rss.instagram.InstagramPost
import ski.rss.instagram.InstagramProfile
import ski.rss.instagram.Result
import java.net.URI
import kotlin.test.Test
import kotlin.test.assertEquals

class InstagramJsonParserTest {
    private val deserializer = InstagramJsonParser(URI("http://instagram.example.com"))

    @Test
    fun readProfile() {
        val profileJson = javaClass.getResource("/finnsadventures.json").readText()

        val result = deserializer.readProfile(profileJson)

        val expectedResult = InstagramProfile(
            name = "finnsadventures",
            description = "Here is my biography",
            link = URI("http://instagram.example.com/finnsadventures"),
            imageUrl = URI("http://example.com/hq_photo"),
            posts = listOf(
                InstagramPost(
                    title = "Asbury Park Convention Hall",
                    description = """
                        <img src="https://instagram.example.com/display.jpg"/>

                        Asbury Park Convention Hall description
                    """.trimIndent(),
                    link = URI("http://instagram.example.com/p/Bx7b96cHeVs"),
                )
            ),
        )
        require(result is Result.Success)
        assertEquals(expectedResult, result.value)
    }

    @Test
    fun invalidJson() {
        val result = deserializer.readProfile("<not-json>")

        require(result is Result.Failure)
        assertEquals("Failed to parse JSON from Instagram response.", result.reason)
    }
}