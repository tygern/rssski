package ski.rss.socialworker

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import redis.clients.jedis.exceptions.JedisConnectionException
import ski.rss.instagram.feed.InstagramAccount
import ski.rss.redissupport.JedisPoolProvider
import ski.rss.socialaccount.SocialAccount
import ski.rss.twitter.feed.TwitterAccount
import ski.rss.workersupport.WorkFinder

class SocialWorkFinder(private val jedisPool: JedisPoolProvider) : WorkFinder<SocialAccount> {
    companion object {
        private val logger: Logger = LoggerFactory.getLogger(this::class.java)
    }

    override fun findRequested(): List<SocialAccount> = try {
        jedisPool.useResource { smembers("feeds") }
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
    } catch (e: JedisConnectionException) {
        logger.error("Failed to connect to Redis: {}", e)
        listOf()
    }
}
