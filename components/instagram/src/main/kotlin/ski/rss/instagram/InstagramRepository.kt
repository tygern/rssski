package ski.rss.instagram

import redis.clients.jedis.JedisPool

class InstagramResponseRepository(private val jedisPool: JedisPool) {
    private val prefix = "instagram"

    fun save(name: String, response: String?) {
        if (response != null) {
            jedisPool.resource.use {
                it.set("$prefix:$name", response)
            }
        }
    }

    fun fetch(name: String): String? =
        jedisPool.resource.use {
            it.get("$prefix:$name")
        }
}
