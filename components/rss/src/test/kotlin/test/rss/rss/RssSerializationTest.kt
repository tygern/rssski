package test.rss.rss

import org.junit.Test
import ski.rss.rss.Item
import ski.rss.rss.Rss
import ski.rss.rss.serialize
import java.net.URL
import kotlin.test.assertEquals

class RssSerializationTest {
    @Test
    fun testSerialize() {
        val rss = Rss(
            title = "Cooking for birds",
            url = URL("https://cfb.example.com"),
            description = "It's for the birds",
            imageUrl = URL("https://cfb.example.com/bird.jpg"),
            items = listOf(
                Item(
                    title = "Seeds 101",
                    url = URL("https://cfb.example.com/seeds.html"),
                    description = "Seed basics",
                    author = "Christopher Wren",
                ),
                Item(
                    title = "Millet 304",
                    url = URL("https://cfb.example.com/millet.html"),
                    description = "Get your beak around millet",
                    author = "Florence Nightingale",
                ),
            ),
        )

        val xml = rss.serialize()

        val expectedXml = """
            |<rss version="2.0">
                |<channel>
                    |<title>Cooking for birds</title>
		            |<link>https://cfb.example.com</link>
		            |<description>It's for the birds</description>
                    |<image>
                        |<title>Cooking for birds</title>
		                |<link>https://cfb.example.com</link>
		                |<url>https://cfb.example.com/bird.jpg</url>
                    |</image>
                    |<item>
                        |<title>Seeds 101</title>
                        |<link>https://cfb.example.com/seeds.html</link>
                        |<description>Seed basics</description>
                        |<author>Christopher Wren</author>
                        |<guid>https://cfb.example.com/seeds.html</guid>
                    |</item>
                    |<item>
                        |<title>Millet 304</title>
                        |<link>https://cfb.example.com/millet.html</link>
                        |<description>Get your beak around millet</description>
                        |<author>Florence Nightingale</author>
                        |<guid>https://cfb.example.com/millet.html</guid>
                    |</item>
                |</channel>
            |</rss>
            """.trimMargin()

        assertEquals(expectedXml, xml)
    }
}
