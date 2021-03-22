package ski.rss

import io.ktor.application.*
import io.ktor.features.*
import io.ktor.locations.*
import io.ktor.routing.*
import io.ktor.serialization.*
import io.ktor.server.engine.*
import io.ktor.server.jetty.*
import io.ktor.util.*
import kotlinx.serialization.json.Json
import ski.rss.instagram.feed.InstagramContentService
import ski.rss.instagram.feed.InstagramJsonParser
import ski.rss.instagram.rss.instagramRss
import ski.rss.redissupport.JedisPoolProvider
import ski.rss.redissupport.StaticRedisUrlProvider
import ski.rss.socialaccount.SocialAccountRepository
import ski.rss.socialaccount.SocialContentRepository
import ski.rss.twitter.feed.TwitterContentService
import ski.rss.twitter.feed.TwitterJsonParser
import ski.rss.twitter.rss.twitterRss
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

    val jedisPool = JedisPoolProvider(StaticRedisUrlProvider(redisUrl))

    val accountRepository = SocialAccountRepository(jedisPool)
    val contentRepository = SocialContentRepository(jedisPool)

    val twitterContentService = TwitterContentService(
        jsonParser = TwitterJsonParser(),
        contentRepository = contentRepository,
        accountRepository = accountRepository,
    )

    val instagramContentService = InstagramContentService(
        jsonParser = InstagramJsonParser(),
        contentRepository = contentRepository,
        accountRepository = accountRepository,
    )

    install(Routing) {
        instagramRss(instagramContentService)
        twitterRss(twitterContentService)
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
