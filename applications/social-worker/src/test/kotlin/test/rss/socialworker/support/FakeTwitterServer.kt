package test.rss.socialworker.support

import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.request.header
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.jetty.Jetty
import java.net.URI

class FakeTwitterServer(port: Int) {
    val url = URI("http://127.0.0.1:$port")
    val validBearerToken = "twitter-bearer-token"

    private val server = embeddedServer(factory = Jetty, port = port) {
        routing {
            get("/2/tweets/search/recent") {
                if (
                    call.parameters["query"] != "from:chelseafc"
                    || call.parameters["max_results"] != "60"
                    || call.parameters["expansions"] != "author_id,attachments.media_keys"
                    || call.parameters["user.fields"] != "name,description,profile_image_url"
                    || call.parameters["tweet.fields"] != "created_at,attachments"
                    || call.parameters["media.fields"] != "preview_image_url,url"
                ) {
                    call.respond(HttpStatusCode.BadRequest, "You're missing some query parameters or the auth ")
                }
                if (call.request.header("Authorization") != "Bearer $validBearerToken") {
                    call.respond(HttpStatusCode.Unauthorized, "You're missing the bearer token, or it's not valid")
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
