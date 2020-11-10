package ski.rss

import io.ktor.http.content.resource
import io.ktor.http.content.resources
import io.ktor.http.content.static
import io.ktor.routing.Route

fun Route.staticContent() {
    resource("/", "static/index.html")
    static {
        resources("static")
    }
}
