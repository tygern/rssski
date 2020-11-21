package ski.rss.twitter

import ski.rss.socialaccount.SocialAccount

data class TwitterAccount(override val username: String) : SocialAccount(username, "twitter")
