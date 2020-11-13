package ski.rss.instagramfeed

import io.ktor.application.call
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.locations.KtorExperimentalLocationsAPI
import io.ktor.locations.Location
import io.ktor.locations.get
import io.ktor.response.respondText
import io.ktor.routing.Route
import ski.rss.functionalsupport.Failure
import ski.rss.instagram.InstagramProfileService
import ski.rss.functionalsupport.Success
import ski.rss.rss.serialize

@KtorExperimentalLocationsAPI
@Location("/instagram/{name}")
data class InstagramFeedPath(val name: String)

@KtorExperimentalLocationsAPI
fun Route.instagramFeed(profileService: InstagramProfileService) {
    get<InstagramFeedPath> {
        when (val result = profileService.fetch(it.name)) {
            is Success -> {
                call.respondText(
                    text = rssFromProfile(result.value).serialize(),
                    contentType = ContentType.Application.Rss,
                )
            }
            is Failure -> {
                call.respondText(
                    text = result.reason,
                    status = HttpStatusCode.ServiceUnavailable
                )
            }
        }
    }
}
