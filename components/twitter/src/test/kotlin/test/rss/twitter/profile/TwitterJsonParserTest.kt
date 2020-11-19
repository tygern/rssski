package test.rss.twitter.profile

import ski.rss.functionalsupport.Failure
import ski.rss.functionalsupport.Success
import ski.rss.twitter.profile.Tweet
import ski.rss.twitter.profile.TwitterJsonParser
import ski.rss.twitter.profile.TwitterProfile
import java.net.URI
import java.time.Instant
import kotlin.test.Test
import kotlin.test.assertEquals

class TwitterJsonParserTest {
    private val parser = TwitterJsonParser()

    @Test
    fun readProfile() {
        val profileJson = javaClass.getResource("/twitter-chelseafc.json").readText()

        val result = parser.readProfile(profileJson)

        val expectedResult = TwitterProfile(
            name = "Chelsea FC",
            username = "@ChelseaFC",
            description = "Welcome to the official Twitter account of Chelsea Football Club. Download our app, the 5th Stand! \uD83D\uDCF2",
            link = URI("https://www.twitter.com/ChelseaFC"),
            imageUrl = URI("https://pbs.twimg.com/profile_images/1326853447044509697/9DtwRsdK_normal.jpg"),
            tweets = listOf(
                Tweet(
                    description = "Our Blues! \uD83D\uDC99\uD83D\uDC4F Goals galore from across the planet today! https://t.co/aGz51KLVoK",
                    mediaPreviewUrls = listOf(
                        URI("https://pbs.twimg.com/media/EnDtCvwWEAEoAUM.jpg"),
                        URI("https://pbs.twimg.com/media/EnDtCvtWMAgYPn7.jpg"),
                    ),
                    link = URI("https://www.twitter.com/ChelseaFC/status/1328822533005709318"),
                    tweetedAt = Instant.ofEpochSecond(1605650954),
                ),
                Tweet(
                    description = "Another @Calteck10 goal for @England's #YoungLions! \uD83E\uDD81 https://t.co/fN5SKyJ0ir",
                    mediaPreviewUrls = listOf(
                        URI("https://pbs.twimg.com/media/EnDlyO8W8AcNllo.jpg"),
                    ),
                    link = URI("https://www.twitter.com/ChelseaFC/status/1328821970931240963"),
                    tweetedAt = Instant.ofEpochSecond(1605650820),
                ),
                Tweet(
                    description = "Still plenty to play for on the international scene! \uD83C\uDF0E",
                    mediaPreviewUrls = listOf(),
                    link = URI("https://www.twitter.com/ChelseaFC/status/1328754629262385159"),
                    tweetedAt = Instant.ofEpochSecond(1605634765),
                ),
            ),
        )

        require(result is Success)
        assertEquals(expectedResult, result.value)
    }

    @Test
    fun invalidJson() {
        val result = parser.readProfile("<not-json>")

        require(result is Failure)
        assertEquals("Failed to parse JSON from Twitter response.", result.reason)
    }

    @Test
    fun jsonWithMissingProperties() {
        val result = parser.readProfile("{}")

        require(result is Failure)
        assertEquals("Failed to parse JSON from Twitter response.", result.reason)
    }
}