package ski.rss.redissupport

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import redis.clients.jedis.Jedis
import redis.clients.jedis.JedisPool
import redis.clients.jedis.JedisPoolConfig
import redis.clients.jedis.Protocol
import redis.clients.jedis.exceptions.JedisConnectionException
import ski.rss.functionalsupport.Failure
import ski.rss.functionalsupport.Success
import java.net.URI

class JedisPoolProvider(redisUrlProvider: RedisUrlProvider) {
    private var jedisPool: JedisPool

    init {
        val redisUrl = when (val redisUrlResult = redisUrlProvider.fetchUrl()) {
            is Success -> redisUrlResult.value
            is Failure -> throw UnableToCreateJedisPoolException(redisUrlResult.reason)
        }

        jedisPool = jedisPool(redisUrl)
    }

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(this::class.java)
    }

    fun <T> useResource(block: Jedis.() -> T): T = jedisPool.resource.use(block)

    private fun jedisPool(redisUrl: URI): JedisPool {
        val poolConfig = JedisPoolConfig().apply {
            maxTotal = 10
            maxIdle = 5
            minIdle = 1
            testOnBorrow = true
            testOnReturn = true
            testWhileIdle = true
        }

        return try {
            JedisPool(
                poolConfig,
                redisUrl.host,
                redisUrl.port,
                Protocol.DEFAULT_TIMEOUT,
                redisUrl.userInfo?.split(":")?.get(1)
            ).apply {
                resource.use { it.ping() }
            }
        } catch (e: JedisConnectionException) {
            logger.error(e.message)

            throw UnableToConnectToRedisException("Could not connect to Redis at $redisUrl")
        }
    }
}

class UnableToCreateJedisPoolException(message: String) : java.lang.RuntimeException(message)
class UnableToConnectToRedisException(message: String) : java.lang.RuntimeException(message)
