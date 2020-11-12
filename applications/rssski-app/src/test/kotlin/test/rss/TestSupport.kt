package test.rss

import io.ktor.locations.KtorExperimentalLocationsAPI
import io.ktor.server.testing.TestApplicationEngine
import io.ktor.server.testing.withTestApplication
import io.ktor.util.KtorExperimentalAPI
import ski.rss.module
import java.net.URI


@KtorExperimentalAPI
@KtorExperimentalLocationsAPI
fun testApp(callback: TestApplicationEngine.() -> Unit) {
    withTestApplication({
        module(
            redisUrl = URI("redis://127.0.0.1:6379"),
            forceHttps = false
        )
    }) { callback() }
}
