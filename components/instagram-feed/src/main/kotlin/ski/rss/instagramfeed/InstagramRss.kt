package ski.rss.instagramfeed

import ski.rss.instagram.InstagramPost
import ski.rss.instagram.InstagramProfile
import ski.rss.rss.Item
import ski.rss.rss.Rss

fun rssFromProfile(profile: InstagramProfile) = Rss(
    title = profile.name,
    description = profile.description,
    url = profile.link,
    imageUrl = profile.imageUrl,
    items = profile.posts.map {
        itemFromPost(it, profile)
    },
)

private fun itemFromPost(post: InstagramPost, profile: InstagramProfile) = Item(
    title = post.title,
    description = post.description,
    imageUrl = post.imageUrl,
    url = post.link,
    author = profile.name,
    pubDate = post.takenAt
)
