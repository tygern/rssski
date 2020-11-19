package ski.rss

import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.features.AutoHeadResponse
import io.ktor.features.CallLogging
import io.ktor.features.ContentNegotiation
import io.ktor.features.DefaultHeaders
import io.ktor.features.HttpsRedirect
import io.ktor.features.XForwardedHeaderSupport
import io.ktor.locations.KtorExperimentalLocationsAPI
import io.ktor.locations.Locations
import io.ktor.routing.Routing
import io.ktor.serialization.DefaultJson
import io.ktor.serialization.json
import io.ktor.server.engine.embeddedServer
import io.ktor.server.jetty.Jetty
import io.ktor.util.KtorExperimentalAPI
import kotlinx.serialization.json.Json
import redis.clients.jedis.JedisPool
import ski.rss.instagram.profile.InstagramJsonParser
import ski.rss.instagram.profile.InstagramProfileRepository
import ski.rss.instagram.profile.InstagramProfileService
import ski.rss.instagram.response.InstagramResponseRepository
import ski.rss.instagramrss.instagramRss
import ski.rss.redissupport.jedisPool
import ski.rss.twitter.profile.TwitterJsonParser
import ski.rss.twitter.profile.TwitterProfileRepository
import ski.rss.twitter.profile.TwitterProfileService
import ski.rss.twitter.response.TwitterResponseRepository
import ski.rss.twitterrss.twitterRss
import java.net.URI

@KtorExperimentalAPI
@KtorExperimentalLocationsAPI
fun Application.module(
    redisUrl: URI,
    forceHttps: Boolean,
) {
    install(DefaultHeaders)
    install(CallLogging)
    install(Locations)
    install(AutoHeadResponse)
    install(ContentNegotiation) {
        json(json = Json(DefaultJson) {
            prettyPrint = true
        })
    }

    if (forceHttps) {
        install(XForwardedHeaderSupport)
        install(HttpsRedirect)
    }

    val jedisPool = jedisPool(redisUrl)

    val instagramProfileService = instagramProfileService(jedisPool)
    val twitterProfileService = twitterProfileService(jedisPool)

    install(Routing) {
        instagramRss(instagramProfileService)
        twitterRss(twitterProfileService)
        info()
        staticContent()
    }
}

@KtorExperimentalAPI
@KtorExperimentalLocationsAPI
fun main() {
    val port = System.getenv("PORT")?.toInt() ?: 8080
    val redisUrl = System.getenv("REDIS_URL")?.let(::URI)
        ?: throw RuntimeException("Please set the REDIS_URL environment variable")
    val forceHttps = System.getenv("FORCE_HTTPS").toBoolean()

    embeddedServer(
        factory = Jetty,
        port = port,
        module = { module(redisUrl, forceHttps) }
    ).start()
}

private fun twitterProfileService(jedisPool: JedisPool): TwitterProfileService {
    val twitterJsonParser = TwitterJsonParser()
    val twitterResponseRepository = TwitterResponseRepository(jedisPool)
    val twitterProfileRepository = TwitterProfileRepository(jedisPool)

    return TwitterProfileService(
        jsonParser = twitterJsonParser,
        responseRepository = twitterResponseRepository,
        profileRepository = twitterProfileRepository,
    )
}

private fun instagramProfileService(jedisPool: JedisPool): InstagramProfileService {
    val instagramJsonParser = InstagramJsonParser()
    val instagramResponseRepository = InstagramResponseRepository(jedisPool)
    val instagramProfileRepository = InstagramProfileRepository(jedisPool)

    return InstagramProfileService(
        jsonParser = instagramJsonParser,
        responseRepository = instagramResponseRepository,
        profileRepository = instagramProfileRepository,
    )
}
