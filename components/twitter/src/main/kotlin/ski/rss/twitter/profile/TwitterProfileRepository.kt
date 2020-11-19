package ski.rss.twitter.profile

import redis.clients.jedis.JedisPool
import ski.rss.twitter.response.twitterPrefix

class TwitterProfileRepository(private val jedisPool: JedisPool) {
    fun save(name: String) {
        jedisPool.resource.use {
            it.sadd("feeds", "$twitterPrefix:$name")
        }
    }
}
