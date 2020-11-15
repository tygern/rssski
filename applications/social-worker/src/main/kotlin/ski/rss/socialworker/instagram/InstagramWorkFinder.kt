package ski.rss.socialworker.instagram

import redis.clients.jedis.JedisPool
import ski.rss.workersupport.WorkFinder

class InstagramWorkFinder(private val jedisPool: JedisPool) : WorkFinder<String> {
    private val instagramPrefix = "instagram:"

    override fun findRequested(): List<String> =
        jedisPool.resource
            .use { it.smembers("feeds") }
            .filter { it.startsWith(instagramPrefix) }
            .map { it.removePrefix(instagramPrefix) }
            .toList()
}
