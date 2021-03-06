package ski.rss.rss

import java.net.URI
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

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
    val imageUrls: List<URI>,
    val url: URI,
    val author: String,
    val pubDate: Instant,
    val imagesPosition: ImagePosition
)

enum class ImagePosition {
    TOP, BOTTOM
}

fun Rss.serialize(): String {
    val builder = StringBuilder("""
            |<?xml version = "1.0" encoding="utf-8"?>
            |<rss version="2.0">
                |<channel>
                    |<title>${title.escape()}</title>
		            |<link>${url.escape()}</link>
		            |<description>${description.escape()}</description>
                    |<image>
                        |<title>${title.escape()}</title>
		                |<link>${url.escape()}</link>
		                |<url>${imageUrl.escape()}</url>
                    |</image>
                    |
    """.trimMargin())

    items.forEach {
        builder.append(it.serialize())
    }

    builder.append("""
            |</channel>
        |</rss>
    """.trimMargin())

    return builder.toString()
}

private fun Item.serialize(): String {
    val formatter = DateTimeFormatter.RFC_1123_DATE_TIME.withZone(ZoneOffset.UTC)

    return """
            |<item>
                |<title>${title.escape()}</title>
                |<link>${url.escape()}</link>
                |<description>
                |${buildDescription()}
                |</description>
                |<author>${author.escape()}</author>
                |<guid>${url.escape()}</guid>
                |<pubDate>${formatter.format(pubDate)}</pubDate>
            |</item>
            |
        """.trimMargin()
}

private fun Item.buildDescription(): String {

    return when(imagesPosition) {
        ImagePosition.BOTTOM -> {
            """<![CDATA[
                |${description.escape().replace("\n", "<br>")}
                |<br>
                |${imageUrls.joinToString("\n") { "<br><img src=\"$it\"/>" }}
                |]]>
        """.trimMargin()
        }
        ImagePosition.TOP -> {
            """<![CDATA[
                |${imageUrls.joinToString("\n") { "<img src=\"$it\"/><br>" }}
                |<br>
                |${description.escape().replace("\n", "<br>")}
                |]]>
        """.trimMargin()
        }
    }
}

private fun xmlEscape(string: String) = "<![CDATA[$string]]>"

private fun String.escape(): String {
    val text = this@escape
    if (text.isEmpty()) return text

    return buildString(length) {
        for (element in text) {
            when (element) {
                '\'' -> append("&#x27;")
                '\"' -> append("&quot;")
                '&' -> append("&amp;")
                '<' -> append("&lt;")
                '>' -> append("&gt;")
                else -> append(element)
            }
        }
    }
}

private fun URI.escape(): String = toString().escape()
