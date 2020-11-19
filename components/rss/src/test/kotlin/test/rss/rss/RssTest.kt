package test.rss.rss

import ski.rss.rss.ImagePosition
import ski.rss.rss.Item
import ski.rss.rss.Rss
import ski.rss.rss.serialize
import java.net.URI
import java.time.Instant
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class RssTest {
    @Test
    fun testSerialize() {
        val rss = Rss(
            title = "Cooking for birds",
            url = URI("https://cfb.example.com"),
            description = "It is for the birds",
            imageUrl = URI("https://cfb.example.com/bird.jpg"),
            items = listOf(
                Item(
                    title = "Seeds 101",
                    url = URI("https://cfb.example.com/seeds.html"),
                    description = "Seed basics\nAll about hulls",
                    imageUrls = listOf(
                        URI("https://images.example.com/seeds.jpg"),
                        URI("https://images.example.com/millet.jpg"),
                    ),
                    author = "Christopher Wren",
                    pubDate = Instant.ofEpochSecond(1605101774),
                    imagesPosition = ImagePosition.TOP,
                ),
                Item(
                    title = "Millet 304",
                    url = URI("https://cfb.example.com/millet.html"),
                    description = "Get your beak around millet",
                    imageUrls = listOf(),
                    author = "Florence Nightingale",
                    pubDate = Instant.ofEpochSecond(1603101774),
                    imagesPosition = ImagePosition.TOP,
                ),
            ),
        )

        val xml = rss.serialize()

        val expectedXml = """
            |<?xml version = "1.0" encoding="utf-8"?>
            |<rss version="2.0">
                |<channel>
                    |<title>Cooking for birds</title>
		            |<link>https://cfb.example.com</link>
		            |<description>It is for the birds</description>
                    |<image>
                        |<title>Cooking for birds</title>
		                |<link>https://cfb.example.com</link>
		                |<url>https://cfb.example.com/bird.jpg</url>
                    |</image>
                    |<item>
                        |<title>Seeds 101</title>
                        |<link>https://cfb.example.com/seeds.html</link>
                        |<description>
                            |<![CDATA[
                            |<img src="https://images.example.com/seeds.jpg"/><br>
                            |<img src="https://images.example.com/millet.jpg"/><br>
                            |<br>
                            |Seed basics<br>All about hulls
                            |]]>
                        |</description>
                        |<author>Christopher Wren</author>
                        |<guid>https://cfb.example.com/seeds.html</guid>
                        |<pubDate>Wed, 11 Nov 2020 13:36:14 GMT</pubDate>
                    |</item>
                    |<item>
                        |<title>Millet 304</title>
                        |<link>https://cfb.example.com/millet.html</link>
                        |<description>
                            |<![CDATA[
                            |
                            |<br>
                            |Get your beak around millet
                            |]]>
                        |</description>
                        |<author>Florence Nightingale</author>
                        |<guid>https://cfb.example.com/millet.html</guid>
                        |<pubDate>Mon, 19 Oct 2020 10:02:54 GMT</pubDate>
                    |</item>
                |</channel>
            |</rss>
            """.trimMargin()

        assertEquals(expectedXml, xml)
    }

    @Test
    fun testSerializeBottomImages() {
        val rss = Rss(
            title = "Cooking for birds",
            url = URI("https://cfb.example.com"),
            description = "It is for the birds",
            imageUrl = URI("https://cfb.example.com/bird.jpg"),
            items = listOf(
                Item(
                    title = "Seeds 101",
                    url = URI("https://cfb.example.com/seeds.html"),
                    description = "Seed basics\nAll about hulls",
                    imageUrls = listOf(
                        URI("https://images.example.com/millet.jpg"),
                    ),
                    author = "Christopher Wren",
                    pubDate = Instant.ofEpochSecond(1605101774),
                    imagesPosition = ImagePosition.BOTTOM,
                )
            ),
        )

        val xml = rss.serialize()

        assertTrue(xml.contains("""
            |<![CDATA[
            |Seed basics<br>All about hulls
            |<br>
            |<br><img src="https://images.example.com/millet.jpg"/>
            |]]>
        """.trimMargin()))
    }

    @Test
    fun testEscapeCharactersSerialize() {
        val rss = Rss(
            title = "Birds & their \"nests\"",
            url = URI("https://cfb.example.com"),
            description = "It's <awesome>",
            imageUrl = URI("https://cfb.example.com/bird.jpg"),
            items = listOf(),
        )

        val xml = rss.serialize()

        val expectedXml = """
            |<?xml version = "1.0" encoding="utf-8"?>
            |<rss version="2.0">
                |<channel>
                    |<title>Birds &amp; their &quot;nests&quot;</title>
		            |<link>https://cfb.example.com</link>
		            |<description>It&#x27;s &lt;awesome&gt;</description>
                    |<image>
                        |<title>Birds &amp; their &quot;nests&quot;</title>
		                |<link>https://cfb.example.com</link>
		                |<url>https://cfb.example.com/bird.jpg</url>
                    |</image>
                |</channel>
            |</rss>
            """.trimMargin()

        assertEquals(expectedXml, xml)
    }
}
