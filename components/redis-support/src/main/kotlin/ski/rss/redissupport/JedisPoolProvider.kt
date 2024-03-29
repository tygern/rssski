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
import java.util.*
import kotlin.concurrent.fixedRateTimer
import kotlin.time.Duration

class JedisPoolProvider(private val redisUrlProvider: RedisUrlProvider) {
    private val logger: Logger = LoggerFactory.getLogger(this::class.java)
    private var jedisPool: JedisPool
    private var redisUrl: URI
    private val poolConfig = JedisPoolConfig().apply {
        maxTotal = 10
        maxIdle = 5
        minIdle = 1
        testOnBorrow = true
        testOnReturn = true
        testWhileIdle = true
    }

    init {
        redisUrl = fetchRedisUrl()
        jedisPool = buildJedisPool(redisUrl)
    }

    fun <T> useResource(block: Jedis.() -> T): T = jedisPool.resource.use(block)

    fun updateEvery(interval: Duration): Timer = fixedRateTimer(
        name = "Update Jedis pool",
        initialDelay = interval.toLongMilliseconds(),
        period = interval.toLongMilliseconds(),
        action = { updateJedisPool() },
    )

    private fun updateJedisPool(): Unit =
        when (val newRedisUrl = fetchRedisUrl()) {
            redisUrl -> logger.info("Redis URL is up-to-date")
            else -> {
                logger.info("Updating Redis URL to $redisUrl")
                redisUrl = newRedisUrl
                jedisPool = buildJedisPool(redisUrl)
            }
        }

    private fun fetchRedisUrl(): URI =
        when (val redisUrlResult = redisUrlProvider.fetchUrl()) {
            is Success -> redisUrlResult.value
            is Failure -> throw UnableToCreateJedisPoolException(redisUrlResult.reason)
        }

    private fun buildJedisPool(redisUrl: URI): JedisPool =
        JedisPool(
            poolConfig,
            redisUrl.host,
            redisUrl.port,
            Protocol.DEFAULT_TIMEOUT,
            redisUrl.userInfo?.split(":")?.get(1)
        ).apply {
            try {
                resource.use { it.ping() }
            } catch (e: JedisConnectionException) {
                logger.error("Could not connect to Redis at {}", redisUrl)
                logger.error(e.message)
            }
        }
}

class UnableToCreateJedisPoolException(message: String) : java.lang.RuntimeException(message)
