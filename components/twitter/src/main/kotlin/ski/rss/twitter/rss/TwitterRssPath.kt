package ski.rss.twitter.rss

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
import ski.rss.rss.serialize
import ski.rss.twitter.feed.TwitterAccount
import ski.rss.twitter.feed.TwitterContentService

@KtorExperimentalLocationsAPI
@Location("/twitter/{name}")
data class TwitterRssPath(val name: String)

@KtorExperimentalLocationsAPI
fun Route.twitterRss(
    contentService: TwitterContentService,
) {
    get<TwitterRssPath> {
        val account = TwitterAccount(it.name)

        when (val result = contentService.fetch(account)) {
            is Success -> {
                call.respondText(
                    text = rssFromContent(result.value).serialize(),
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

    post<TwitterRssPath> {
        val account = TwitterAccount(it.name)

        contentService.subscribe(account)

        call.respond(HttpStatusCode.NoContent)
    }
}
