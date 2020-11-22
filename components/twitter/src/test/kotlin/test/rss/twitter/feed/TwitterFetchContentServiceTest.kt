package test.rss.twitter.feed

import io.mockk.coEvery
import io.mockk.mockk
import ski.rss.functionalsupport.Failure
import ski.rss.functionalsupport.Success
import ski.rss.socialaccount.AccountContentRepository
import ski.rss.twitter.feed.TwitterAccount
import ski.rss.twitter.feed.TwitterFetchContentService
import ski.rss.twitter.feed.TwitterJsonParser
import kotlin.test.Test
import kotlin.test.assertEquals

class TwitterFetchContentServiceTest {
    private val contentRepository: AccountContentRepository = mockk(relaxUnitFun = true)

    private val service = TwitterFetchContentService(
        jsonParser = TwitterJsonParser(),
        contentRepository = contentRepository,
    )

    private val account = TwitterAccount("chelseafc")

    @Test
    fun testFetchProfile() {
        coEvery { contentRepository.fetch(account) } returns javaClass.getResource("/twitter-chelseafc.json").readText()

        val result = service.fetch(account)

        require(result is Success)
        assertEquals("@ChelseaFC", result.value.username)
    }

    @Test
    fun testFetchNotFound() {
        coEvery { contentRepository.fetch(account) } returns null

        val result = service.fetch(account)

        require(result is Failure)
        assertEquals("Feed not found for account twitter:chelseafc.", result.reason)
    }

    @Test
    fun testFetchJsonParseError() {
        coEvery { contentRepository.fetch(account) } returns "<junk>"

        val result = service.fetch(account)

        require(result is Failure)
        assertEquals("Failed to parse JSON from Twitter response.", result.reason)
    }
}