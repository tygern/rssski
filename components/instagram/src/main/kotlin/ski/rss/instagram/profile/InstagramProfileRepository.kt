package ski.rss.instagram.profile

import redis.clients.jedis.JedisPool
import ski.rss.instagram.response.instagramPrefix

class InstagramProfileRepository(private val jedisPool: JedisPool) {
    fun save(name: String) {
        jedisPool.resource.use {
            it.sadd("feeds", "$instagramPrefix:$name")
        }
    }
}
