package test.rss.instagramfeed

import ski.rss.instagram.InstagramPost
import ski.rss.instagram.InstagramProfile
import ski.rss.instagramfeed.rssFromProfile
import ski.rss.rss.Item
import ski.rss.rss.Rss
import java.net.URI
import kotlin.test.Test
import kotlin.test.assertEquals

class InstagramRssTest {
    @Test
    fun writesFeed() {
        val profile = InstagramProfile(
            name = "sheep",
            description = "I like sheep",
            link = URI("http://instagram.example.com/sheep"),
            imageUrl = URI("http://example.com/hq_photo_of_sheep"),
            posts = listOf(
                InstagramPost(
                    title = "Grazing",
                    description = "Grazing is fun",
                    link = URI("http://instagram.example.com/p/GRAZE"),
                ),
                InstagramPost(
                    title = "Baaaing",
                    description = "Baaaing is fun",
                    link = URI("http://instagram.example.com/p/BAAA"),
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
                        url = URI("http://instagram.example.com/p/GRAZE"),
                        author = "sheep",
                    ),
                    Item(
                        title = "Baaaing",
                        description = "Baaaing is fun",
                        url = URI("http://instagram.example.com/p/BAAA"),
                        author = "sheep",
                    ),
                ),
            ),
            result
        )
    }
}