package ski.rss.twitter.feed

import java.net.URI
import java.time.Instant

data class TwitterContent(
    val name: String,
    val username: String,
    val description: String,
    val link: URI,
    val imageUrl: URI,
    val tweets: List<Tweet>,
)

data class Tweet(
    val description: String,
    val mediaPreviewUrls: List<URI>,
    val link: URI,
    val tweetedAt: Instant,
)
