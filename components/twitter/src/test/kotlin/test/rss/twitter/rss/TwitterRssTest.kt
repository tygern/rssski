package test.rss.twitter.rss

import ski.rss.rss.ImagePosition
import ski.rss.rss.Item
import ski.rss.rss.Rss
import ski.rss.twitter.response.Tweet
import ski.rss.twitter.response.TwitterProfile
import ski.rss.twitter.rss.rssFromProfile
import java.net.URI
import java.time.Instant
import kotlin.test.Test
import kotlin.test.assertEquals

class TwitterRssTest {
    @Test
    fun testRssFromProfile() {
        val profile = TwitterProfile(
            name = "Finn's Adventures",
            username = "@finn",
            description = "Some cool adventures",
            link = URI("https://twitter.example.com/finn"),
            imageUrl = URI("https://twitter.example.com/finn/photo.jpg"),
            tweets = listOf(
                Tweet(
                    description = "Here is my second tweet",
                    mediaPreviewUrls = listOf(
                        URI("https://twitter.example.com/finn/preview-1.jpg"),
                        URI("https://twitter.example.com/finn/preview-2.jpg"),
                    ),
                    link = URI("https://twitter.example.com/finn/status/1112"),
                    tweetedAt = Instant.ofEpochSecond(1605699999),
                ),
                Tweet(
                    description = "Here is my first tweet",
                    mediaPreviewUrls = listOf(),
                    link = URI("https://twitter.example.com/finn/status/1111"),
                    tweetedAt = Instant.ofEpochSecond(1605611111),
                ),
            ),
        )

        val result = rssFromProfile(profile)

        val expectedResult = Rss(
            title = "Finn's Adventures",
            description = "Some cool adventures",
            url = URI("https://twitter.example.com/finn"),
            imageUrl = URI("https://twitter.example.com/finn/photo.jpg"),
            items = listOf(
                Item(
                    title = "Finn's Adventures (@finn)",
                    description = "Here is my second tweet",
                    imageUrls = listOf(
                        URI("https://twitter.example.com/finn/preview-1.jpg"),
                        URI("https://twitter.example.com/finn/preview-2.jpg"),
                    ),
                    url = URI("https://twitter.example.com/finn/status/1112"),
                    author = "@finn",
                    pubDate = Instant.ofEpochSecond(1605699999),
                    imagesPosition = ImagePosition.BOTTOM,
                ),
                Item(
                    title = "Finn's Adventures (@finn)",
                    description = "Here is my first tweet",
                    imageUrls = listOf(),
                    url = URI("https://twitter.example.com/finn/status/1111"),
                    author = "@finn",
                    pubDate = Instant.ofEpochSecond(1605611111),
                    imagesPosition = ImagePosition.BOTTOM,
                ),
            ),
        )

        assertEquals(expectedResult, result)
    }
}