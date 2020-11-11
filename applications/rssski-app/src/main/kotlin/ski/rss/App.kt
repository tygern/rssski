package ski.rss

import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.features.AutoHeadResponse
import io.ktor.features.CallLogging
import io.ktor.features.ContentNegotiation
import io.ktor.features.DefaultHeaders
import io.ktor.locations.KtorExperimentalLocationsAPI
import io.ktor.locations.Locations
import io.ktor.routing.Routing
import io.ktor.serialization.DefaultJson
import io.ktor.serialization.json
import io.ktor.server.engine.embeddedServer
import io.ktor.server.jetty.Jetty
import io.ktor.util.KtorExperimentalAPI
import kotlinx.serialization.json.Json
import ski.rss.instagram.InstagramJsonParser
import ski.rss.instagram.InstagramProfileService
import ski.rss.instagram.InstagramResponseRepository
import ski.rss.instagramfeed.instagramFeed
import ski.rss.redissupport.jedisPool
import java.net.URI

@KtorExperimentalAPI
@KtorExperimentalLocationsAPI
fun Application.module(redisUrl: URI) {
    install(DefaultHeaders)
    install(CallLogging)
    install(Locations)
    install(AutoHeadResponse)
    install(ContentNegotiation) {
        json(json = Json(DefaultJson) {
            prettyPrint = true
        })
    }

    val jedisPool = jedisPool(redisUrl)

    val instagramJsonParser = InstagramJsonParser()
    val instagramResponseRepository = InstagramResponseRepository(jedisPool)

    val instagramService = InstagramProfileService(
        jsonParser = instagramJsonParser,
        responseRepository = instagramResponseRepository
    )

    install(Routing) {
        instagramFeed(instagramService)
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

    embeddedServer(
        factory = Jetty,
        port = port,
        module = { module(redisUrl) }
    ).start()
}
