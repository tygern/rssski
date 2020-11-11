package test.rss

import io.ktor.locations.KtorExperimentalLocationsAPI
import io.ktor.server.testing.TestApplicationEngine
import io.ktor.server.testing.withTestApplication
import ski.rss.module


@KtorExperimentalLocationsAPI
fun testApp(callback: TestApplicationEngine.() -> Unit) {
    withTestApplication({ module() }) { callback() }
}
