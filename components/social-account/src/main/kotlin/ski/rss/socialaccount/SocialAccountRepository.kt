package ski.rss.socialaccount

import ski.rss.redissupport.JedisPoolProvider

class SocialAccountRepository(private val jedisPool: JedisPoolProvider) {
    fun save(account: SocialAccount) {
        jedisPool.useResource {
            sadd("feeds", account.toString())
        }
    }
}
