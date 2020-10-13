package ski.rss

import io.ktor.application.call
import io.ktor.locations.KtorExperimentalLocationsAPI
import io.ktor.locations.Location
import io.ktor.locations.get
import io.ktor.response.respond
import io.ktor.routing.Route

@KtorExperimentalLocationsAPI
@Location("/")
class IndexPath

@KtorExperimentalLocationsAPI
fun Route.index() {
    get<IndexPath> {
        call.respond(mapOf("application" to "rssski"))
    }
}
