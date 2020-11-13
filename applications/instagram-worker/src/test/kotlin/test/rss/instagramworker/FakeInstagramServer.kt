package test.rss.instagramworker

import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.jetty.Jetty
import java.net.URI

class FakeInstagramServer(port: Int) {
    val url = URI("http://127.0.0.1:$port")

    private val server = embeddedServer(factory = Jetty, port = port) {
        routing {
            get("/finnsadventures") {
                if (call.parameters["__a"] != "1") {
                    call.respond(HttpStatusCode.BadRequest)
                }

                call.respondText("{\"some\": \"json\"}")
            }
        }
    }

    fun start() {
        server.start()
    }

    fun stop() {
        server.stop(10, 1_000)
    }
}
