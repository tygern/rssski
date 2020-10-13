package ski.rss

import io.ktor.application.Application
import io.ktor.application.install
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
import kotlinx.serialization.json.Json

@KtorExperimentalLocationsAPI
fun Application.module() {
    install(DefaultHeaders)
    install(CallLogging)
    install(Locations)
    install(ContentNegotiation) {
        json(json = Json(DefaultJson) {
            prettyPrint = true
        })
    }

    install(Routing) {
        index()
    }
}

@KtorExperimentalLocationsAPI
fun main() {
    val port = System.getenv("PORT")?.toInt() ?: 8080

    embeddedServer(
        factory = Jetty,
        port = port,
        module = Application::module
    ).start()
}
