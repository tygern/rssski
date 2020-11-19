package test.rss.instagram.profile

import ski.rss.functionalsupport.Failure
import ski.rss.functionalsupport.Success
import ski.rss.instagram.profile.InstagramJsonParser
import ski.rss.instagram.profile.InstagramPost
import ski.rss.instagram.profile.InstagramProfile
import java.net.URI
import java.time.Instant
import kotlin.test.Test
import kotlin.test.assertEquals

class InstagramJsonParserTest {
    private val parser = InstagramJsonParser()

    @Test
    fun readProfile() {
        val profileJson = javaClass.getResource("/finnsadventures.json").readText()

        val result = parser.readProfile(profileJson)

        val expectedResult = InstagramProfile(
            name = "finnsadventures",
            description = "Here is my biography",
            link = URI("https://www.instagram.com/finnsadventures"),
            imageUrl = URI("http://example.com/hq_photo"),
            posts = listOf(
                InstagramPost(
                    title = "Asbury Park Convention Hall",
                    description = "Asbury Park Convention Hall description",
                    imageUrl = URI("https://instagram.example.com/display.jpg"),
                    link = URI("https://www.instagram.com/p/Bx7b96cHeVs"),
                    takenAt = Instant.ofEpochSecond(1605101774),
                ),
                InstagramPost(
                    title = "What a description! Very nice and descri…",
                    description = "What a description! Very nice and descriptive and a bit long.",
                    imageUrl = URI("https://instagram.example.com/anotherdisplay.jpg"),
                    link = URI("https://www.instagram.com/p/CHGTddbDpZb"),
                    takenAt = Instant.ofEpochSecond(1604101774),
                )
            ),
        )
        require(result is Success)
        assertEquals(expectedResult, result.value)
    }

    @Test
    fun invalidJson() {
        val result = parser.readProfile("<not-json>")

        require(result is Failure)
        assertEquals("Failed to parse JSON from Instagram response.", result.reason)
    }

    @Test
    fun jsonWithMissingProperties() {
        val result = parser.readProfile("{}")

        require(result is Failure)
        assertEquals("Failed to parse JSON from Instagram response.", result.reason)
    }
}
