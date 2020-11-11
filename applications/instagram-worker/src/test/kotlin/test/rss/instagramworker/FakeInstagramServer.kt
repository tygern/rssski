package test.rss.instagramworker

import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.jetty.Jetty

class FakeInstagramServer(port: Int) {
    private val server = embeddedServer(factory = Jetty, port = port) {
        routing {
            get("/finnsadventures") {
                if (call.parameters["__a"] != "1") {
                    call.respond(HttpStatusCode.BadRequest)
                }

                call.respondText(javaClass.getResource("/finnsadventures.json").readText())
            }
            get("/givemejunk") {
                if (call.parameters["__a"] != "1") {
                    call.respond(HttpStatusCode.BadRequest)
                }

                call.respondText("<junk>")
            }
            get("/") {
                call.respondText("fake instagram")
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
