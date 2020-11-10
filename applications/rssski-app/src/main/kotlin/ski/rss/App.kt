package ski.rss

import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.features.AutoHeadResponse
import io.ktor.features.CallLogging
import io.ktor.features.ContentNegotiation
import io.ktor.features.DefaultHeaders
import io.ktor.http.content.resource
import io.ktor.http.content.resources
import io.ktor.http.content.static
import io.ktor.locations.KtorExperimentalLocationsAPI
import io.ktor.locations.Locations
import io.ktor.routing.Routing
import io.ktor.serialization.DefaultJson
import io.ktor.serialization.json
import io.ktor.server.engine.embeddedServer
import io.ktor.server.jetty.Jetty
import io.ktor.util.KtorExperimentalAPI
import kotlinx.serialization.json.Json
import ski.rss.instagram.InstagramClient
import ski.rss.instagram.InstagramJsonParser
import ski.rss.instagramfeed.instagramFeed
import java.net.URI

@KtorExperimentalAPI
@KtorExperimentalLocationsAPI
fun Application.module(instagramUrl: URI) {
    install(DefaultHeaders)
    install(CallLogging)
    install(Locations)
    install(AutoHeadResponse)
    install(ContentNegotiation) {
        json(json = Json(DefaultJson) {
            prettyPrint = true
        })
    }

    val httpClient = HttpClient(CIO)
    val instagramJsonParser = InstagramJsonParser(instagramUrl)
    val instagramClient = InstagramClient(
        instagramUrl = instagramUrl,
        jsonParser = instagramJsonParser,
        httpClient = httpClient,
    )

    install(Routing) {
        instagramFeed(instagramClient)
        info()
        staticContent()
    }
}

@KtorExperimentalAPI
@KtorExperimentalLocationsAPI
fun main() {
    val port = System.getenv("PORT")?.toInt() ?: 8080
    val instagramUrl = URI(System.getenv("INSTAGRAM_URL") ?: throw RuntimeException("Please set the INSTAGRAM_URL environment variable"))

    embeddedServer(
        factory = Jetty,
        port = port,
        module = { module(instagramUrl = instagramUrl) }
    ).start()
}
