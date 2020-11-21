package ski.rss.instagram.rss

import io.ktor.application.call
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.locations.KtorExperimentalLocationsAPI
import io.ktor.locations.Location
import io.ktor.locations.get
import io.ktor.locations.post
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.routing.Route
import ski.rss.functionalsupport.Failure
import ski.rss.functionalsupport.Success
import ski.rss.instagram.profile.InstagramProfileRepository
import ski.rss.instagram.response.InstagramFeedService
import ski.rss.rss.serialize

@KtorExperimentalLocationsAPI
@Location("/instagram/{name}")
data class InstagramRssPath(val name: String)

@KtorExperimentalLocationsAPI
fun Route.instagramRss(
    feedService: InstagramFeedService,
    profileRepository: InstagramProfileRepository,
) {
    get<InstagramRssPath> {
        when (val result = feedService.fetch(it.name)) {
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

    post<InstagramRssPath> {
        profileRepository.save(it.name)

        call.respond(HttpStatusCode.NoContent)
    }
}
