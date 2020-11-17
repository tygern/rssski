package ski.rss.socialworker

sealed class SocialAccount()

data class InstagramAccount(val name: String) : SocialAccount()
data class TwitterAccount(val name: String) : SocialAccount()
