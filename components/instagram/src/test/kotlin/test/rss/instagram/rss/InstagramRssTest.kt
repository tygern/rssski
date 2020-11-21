package test.rss.instagram.rss

import ski.rss.instagram.response.InstagramPost
import ski.rss.instagram.response.InstagramProfile
import ski.rss.instagram.rss.rssFromProfile
import ski.rss.rss.ImagePosition
import ski.rss.rss.Item
import ski.rss.rss.Rss
import java.net.URI
import java.time.Instant
import kotlin.test.Test
import kotlin.test.assertEquals

class InstagramRssTest {
    @Test
    fun testRssFromProfile() {
        val profile = InstagramProfile(
            name = "sheep",
            description = "I like sheep",
            link = URI("http://instagram.example.com/sheep"),
            imageUrl = URI("http://example.com/hq_photo_of_sheep"),
            posts = listOf(
                InstagramPost(
                    title = "Grazing",
                    description = "Grazing is fun",
                    imageUrl = URI("http://example.com/hq_photo_of_sheep"),
                    link = URI("http://instagram.example.com/p/GRAZE"),
                    takenAt = Instant.ofEpochSecond(1605101774),
                ),
                InstagramPost(
                    title = "Baaaing",
                    description = "Baaaing is fun",
                    imageUrl = URI("http://example.com/hq_photo_of_sheep"),
                    link = URI("http://instagram.example.com/p/BAAA"),
                    takenAt = Instant.ofEpochSecond(1603101774),
                )
            ),
        )

        val result = rssFromProfile(profile)

        assertEquals(
            Rss(
                title = "sheep",
                description = "I like sheep",
                url = URI("http://instagram.example.com/sheep"),
                imageUrl = URI("http://example.com/hq_photo_of_sheep"),
                items = listOf(
                    Item(
                        title = "Grazing",
                        description = "Grazing is fun",
                        imageUrls = listOf(URI("http://example.com/hq_photo_of_sheep")),
                        url = URI("http://instagram.example.com/p/GRAZE"),
                        author = "sheep",
                        pubDate = Instant.ofEpochSecond(1605101774),
                        imagesPosition = ImagePosition.TOP,
                    ),
                    Item(
                        title = "Baaaing",
                        description = "Baaaing is fun",
                        imageUrls = listOf(URI("http://example.com/hq_photo_of_sheep")),
                        url = URI("http://instagram.example.com/p/BAAA"),
                        author = "sheep",
                        pubDate = Instant.ofEpochSecond(1603101774),
                        imagesPosition = ImagePosition.TOP,
                    ),
                ),
            ),
            result
        )
    }
}
