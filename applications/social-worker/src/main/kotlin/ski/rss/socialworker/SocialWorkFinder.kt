package ski.rss.socialworker

import redis.clients.jedis.JedisPool
import ski.rss.workersupport.WorkFinder

class SocialWorkFinder(private val jedisPool: JedisPool) : WorkFinder<String> {
    override fun findRequested(): List<String> =
        jedisPool.resource
            .use { it.smembers("feeds") }
            .toList()
}
