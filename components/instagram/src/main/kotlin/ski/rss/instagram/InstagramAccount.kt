package ski.rss.instagram

import ski.rss.socialaccount.SocialAccount

data class InstagramAccount(override val username: String) : SocialAccount(username, "twitter")
