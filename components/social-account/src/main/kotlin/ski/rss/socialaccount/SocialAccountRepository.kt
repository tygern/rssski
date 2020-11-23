package ski.rss.socialaccount

import redis.clients.jedis.JedisPool

class SocialAccountRepository(private val jedisPool: JedisPool) {
    fun save(account: SocialAccount) {
        jedisPool.resource.use {
            it.sadd("feeds", account.toString())
        }
    }
}
