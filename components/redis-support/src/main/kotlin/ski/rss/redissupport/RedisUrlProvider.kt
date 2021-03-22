package ski.rss.redissupport

import ski.rss.functionalsupport.Result
import java.net.URI

interface RedisUrlProvider {
    fun fetchUrl(): Result<URI>
}