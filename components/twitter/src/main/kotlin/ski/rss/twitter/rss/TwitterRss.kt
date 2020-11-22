package ski.rss.twitter.rss

import ski.rss.rss.ImagePosition
import ski.rss.rss.Item
import ski.rss.rss.Rss
import ski.rss.twitter.feed.TwitterContent

fun rssFromContent(content: TwitterContent): Rss {
    return Rss(
        title = content.name,
        description = content.description,
        url = content.link,
        imageUrl = content.imageUrl,
        items = content.tweets.map { tweet ->
            Item(
                title = "${content.name} (${content.username})",
                description = tweet.description,
                imageUrls = tweet.mediaPreviewUrls,
                url = tweet.link,
                author = content.username,
                pubDate = tweet.tweetedAt,
                imagesPosition = ImagePosition.BOTTOM
            )
        },
    )
}
