package ski.rss.socialaccount

import ski.rss.redissupport.JedisPoolProvider

class SocialContentRepository(private val jedisPool: JedisPoolProvider) {
    fun save(account: SocialAccount, content: String) {
        jedisPool.useResource {
            set(account.toString(), content)
        }
    }

    fun fetch(account: SocialAccount): String? =
        jedisPool.useResource {
            get(account.toString())
        }
}
