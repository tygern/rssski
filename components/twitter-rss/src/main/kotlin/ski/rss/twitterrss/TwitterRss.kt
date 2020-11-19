package ski.rss.twitterrss

import ski.rss.rss.ImagePosition
import ski.rss.rss.Item
import ski.rss.rss.Rss
import ski.rss.twitter.profile.TwitterProfile

fun rssFromProfile(profile: TwitterProfile): Rss {
    return Rss(
        title = profile.name,
        description = profile.description,
        url = profile.link,
        imageUrl = profile.imageUrl,
        items = profile.tweets.map { tweet ->
            Item(
                title = "${profile.name} (${profile.username})",
                description = tweet.description,
                imageUrls = tweet.mediaPreviewUrls,
                url = tweet.link,
                author = profile.username,
                pubDate = tweet.tweetedAt,
                imagesPosition = ImagePosition.BOTTOM
            )
        },
    )
}
