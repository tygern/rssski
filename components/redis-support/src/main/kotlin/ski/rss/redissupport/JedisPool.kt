package ski.rss.redissupport

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import redis.clients.jedis.JedisPool
import redis.clients.jedis.JedisPoolConfig
import redis.clients.jedis.Protocol
import redis.clients.jedis.exceptions.JedisConnectionException
import java.net.URI

class RedisSupport

private val logger: Logger = LoggerFactory.getLogger(RedisSupport::class.java)

fun jedisPool(redisUrl: URI): JedisPool {
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

        throw RuntimeException("Could not connect to Redis at $redisUrl")
    }
}
