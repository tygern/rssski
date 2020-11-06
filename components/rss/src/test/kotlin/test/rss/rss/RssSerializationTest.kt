package test.rss.rss

import org.junit.Test
import ski.rss.rss.Item
import ski.rss.rss.Rss
import ski.rss.rss.serialize
import java.net.URI
import kotlin.test.assertEquals

class RssSerializationTest {
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
                    description = "Seed basics",
                    imageUrl = URI("https://images.example.com/seeds.jpg"),
                    author = "Christopher Wren",
                ),
                Item(
                    title = "Millet 304",
                    url = URI("https://cfb.example.com/millet.html"),
                    description = "Get your beak around millet",
                    imageUrl = URI("https://images.example.com/millet.jpg"),
                    author = "Florence Nightingale",
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
                            |<![CDATA[<img src="https://images.example.com/seeds.jpg"/><br><br>]]>
                            |
                            |Seed basics
                        |</description>
                        |<author>Christopher Wren</author>
                        |<guid>https://cfb.example.com/seeds.html</guid>
                    |</item>
                    |<item>
                        |<title>Millet 304</title>
                        |<link>https://cfb.example.com/millet.html</link>
                        |<description>
                            |<![CDATA[<img src="https://images.example.com/millet.jpg"/><br><br>]]>
                            |
                            |Get your beak around millet
                        |</description>
                        |<author>Florence Nightingale</author>
                        |<guid>https://cfb.example.com/millet.html</guid>
                    |</item>
                |</channel>
            |</rss>
            """.trimMargin()

        assertEquals(expectedXml, xml)
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
