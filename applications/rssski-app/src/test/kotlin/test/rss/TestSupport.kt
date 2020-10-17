package test.rss

import io.ktor.locations.KtorExperimentalLocationsAPI
import io.ktor.server.testing.TestApplicationEngine
import io.ktor.server.testing.withTestApplication
import ski.rss.module
import java.net.URI


@KtorExperimentalLocationsAPI
fun testApp(callback: TestApplicationEngine.() -> Unit) {
    val instagramUrl = URI("http://localhost:8675")
    withTestApplication({ module(instagramUrl) }) { callback() }
}
