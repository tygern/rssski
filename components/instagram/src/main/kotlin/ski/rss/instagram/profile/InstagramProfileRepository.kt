package ski.rss.instagram.profile

import redis.clients.jedis.JedisPool

class InstagramProfileRepository(private val jedisPool: JedisPool) {
    private val prefix = "instagram"

    fun save(name: String) {
        jedisPool.resource.use {
            it.sadd("feeds", "$prefix:$name")
        }
    }
}
