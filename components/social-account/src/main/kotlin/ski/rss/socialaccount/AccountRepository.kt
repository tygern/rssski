package ski.rss.socialaccount

import redis.clients.jedis.JedisPool

class AccountRepository(private val jedisPool: JedisPool) {
    fun save(account: SocialAccount) {
        jedisPool.resource.use {
            it.sadd("feeds", account.toString())
        }
    }
}
