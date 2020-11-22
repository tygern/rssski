package ski.rss.socialworker

import redis.clients.jedis.JedisPool
import ski.rss.instagram.InstagramAccount
import ski.rss.socialaccount.SocialAccount
import ski.rss.twitter.feed.TwitterAccount
import ski.rss.workersupport.WorkFinder

class SocialWorkFinder(private val jedisPool: JedisPool) : WorkFinder<SocialAccount> {
    override fun findRequested(): List<SocialAccount> =
        jedisPool.resource
            .use { it.smembers("feeds") }
            .mapNotNull {
                val prefix = it.split(":")[0]
                val name = it.split(":")[1]

                when (prefix) {
                    "instagram" -> InstagramAccount(name)
                    "twitter" -> TwitterAccount(name)
                    else -> null
                }
            }
            .toList()
}
