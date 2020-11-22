package ski.rss.socialaccount

import redis.clients.jedis.JedisPool

class AccountContentRepository(private val jedisPool: JedisPool) {
    fun save(account: SocialAccount, content: String) {
        jedisPool.resource.use {
            it.set(account.toString(), content)
        }
    }

    fun fetch(account: SocialAccount): String? =
        jedisPool.resource.use {
            it.get(account.toString())
        }
}
