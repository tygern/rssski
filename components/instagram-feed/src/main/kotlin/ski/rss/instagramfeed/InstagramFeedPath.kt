package ski.rss.instagramfeed

import io.ktor.application.call
import io.ktor.http.ContentType
import io.ktor.locations.KtorExperimentalLocationsAPI
import io.ktor.locations.Location
import io.ktor.locations.get
import io.ktor.response.respondText
import io.ktor.routing.Route
import ski.rss.instagram.InstagramClient
import ski.rss.instagram.Result
import ski.rss.rss.serialize

@KtorExperimentalLocationsAPI
@Location("/instagram/{name}")
data class InstagramFeedPath(val name: String)

@KtorExperimentalLocationsAPI
fun Route.instagramFeed(instagramClient: InstagramClient) {
    get<InstagramFeedPath> {
        call.respondText(contentType = ContentType.Application.Rss) {
            when (val result = instagramClient.findFeed(it.name)) {
                is Result.Success -> {
                    rssFromProfile(result.value).serialize()
                }
                is Result.Failure -> {
                    result.reason
                }
            }
        }
    }
}
