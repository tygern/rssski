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
import ski.rss.instagram.feed.InstagramContentService
import ski.rss.instagram.feed.InstagramJsonParser
import ski.rss.instagram.rss.instagramRss
import ski.rss.redissupport.jedisPool
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

    val jedisPool = jedisPool(redisUrl)

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
