package ski.rss.twitter.response

import redis.clients.jedis.JedisPool

const val twitterPrefix = "twitter"

class TwitterResponseRepository(private val jedisPool: JedisPool) {
    fun save(name: String, response: String) {
        jedisPool.resource.use {
            it.set("$twitterPrefix:$name", response)
        }
    }

    fun fetch(name: String): String? =
        jedisPool.resource.use {
            it.get("$twitterPrefix:$name")
        }
}
