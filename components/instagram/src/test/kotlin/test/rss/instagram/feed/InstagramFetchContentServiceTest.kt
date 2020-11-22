package test.rss.instagram.feed

import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import ski.rss.functionalsupport.Failure
import ski.rss.functionalsupport.Success
import ski.rss.instagram.feed.InstagramAccount
import ski.rss.instagram.feed.InstagramFetchContentService
import ski.rss.instagram.feed.InstagramJsonParser
import ski.rss.socialaccount.AccountContentRepository
import kotlin.test.Test
import kotlin.test.assertEquals

@ExperimentalCoroutinesApi
class InstagramFetchContentServiceTest {
    private val contentRepository: AccountContentRepository = mockk(relaxUnitFun = true)

    private val service = InstagramFetchContentService(
        jsonParser = InstagramJsonParser(),
        contentRepository = contentRepository
    )

    private val account = InstagramAccount("finnsadventures")

    @Test
    fun testFetchProfile() {
        coEvery { contentRepository.fetch(account) } returns javaClass.getResource("/finnsadventures.json").readText()

        val result = service.fetch(account)

        require(result is Success)
        assertEquals("finnsadventures", result.value.name)
    }

    @Test
    fun testFetchNotFound() {
        coEvery { contentRepository.fetch(account) } returns null

        val result = service.fetch(account)

        require(result is Failure)
        assertEquals("Feed not found for account instagram:finnsadventures.", result.reason)
    }

    @Test
    fun testFetchJsonParseError() {
        coEvery { contentRepository.fetch(account) } returns "<junk>"

        val result = service.fetch(account)

        require(result is Failure)
        assertEquals("Failed to parse JSON from Instagram response.", result.reason)
    }
}
