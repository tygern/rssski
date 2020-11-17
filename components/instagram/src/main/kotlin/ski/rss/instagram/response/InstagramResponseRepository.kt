package ski.rss.instagram.response

import redis.clients.jedis.JedisPool

const val instagramPrefix = "instagram"

class InstagramResponseRepository(private val jedisPool: JedisPool) {
    fun save(name: String, response: String) {
        jedisPool.resource.use {
            it.set("$instagramPrefix:$name", response)
        }
    }

    fun fetch(name: String): String? =
        jedisPool.resource.use {
            it.get("$instagramPrefix:$name")
        }
}
