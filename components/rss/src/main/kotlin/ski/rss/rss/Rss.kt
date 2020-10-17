package ski.rss.rss

import java.net.URI

data class Rss(
    val title: String,
    val description: String,
    val url: URI,
    val imageUrl: URI,
    val items: List<Item>,
)

data class Item(
    val title: String,
    val description: String,
    val url: URI,
    val author: String,
)

fun Rss.serialize(): String {
    val builder = StringBuilder("""
            |<rss version="2.0">
                |<channel>
                    |<title>${title}</title>
		            |<link>${url}</link>
		            |<description>${description}</description>
                    |<image>
                        |<title>${title}</title>
		                |<link>${url}</link>
		                |<url>${imageUrl}</url>
                    |</image>
                    |
    """.trimMargin())

    items.forEach {
        builder.append("""
            |<item>
                |<title>${it.title}</title>
                |<link>${it.url}</link>
                |<description>${it.description}</description>
                |<author>${it.author}</author>
                |<guid>${it.url}</guid>
            |</item>
            |
        """.trimMargin())
    }

    builder.append("""
            |</channel>
        |</rss>
    """.trimMargin())

    return builder.toString()
}
