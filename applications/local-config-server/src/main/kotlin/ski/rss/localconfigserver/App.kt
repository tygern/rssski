package ski.rss.localconfigserver

import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.jetty.*

fun main() {
    val port = System.getenv("PORT")?.toInt() ?: 8081

    embeddedServer(
        factory = Jetty,
        port = port,
        module = {
            install(DefaultHeaders)
            install(CallLogging)
            install(AutoHeadResponse)
            install(Routing) {
                get("/") {
                    when (val authHeader = call.request.headers["Authorization"]) {
                        "Bearer super-secret" -> call.respondText(contentType = ContentType.Application.Json) {
                            """{"REDIS_URL": "redis://127.0.0.1:6379"}"""
                        }
                        else -> call.respondText(status = HttpStatusCode.Unauthorized) {
                            "Invalid auth header: $authHeader"
                        }
                    }
                }
            }
        }
    ).start()
}
