package ski.rss.redissupport

import ski.rss.functionalsupport.Result
import ski.rss.functionalsupport.Success
import java.net.URI

class StaticRedisUrlProvider(private val redisUrl: URI) : RedisUrlProvider {
    override fun fetchUrl(): Result<URI> = Success(redisUrl)
}
