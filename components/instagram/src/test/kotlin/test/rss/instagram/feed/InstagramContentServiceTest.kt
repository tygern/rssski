package test.rss.instagram.feed

import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import ski.rss.functionalsupport.Failure
import ski.rss.functionalsupport.Success
import ski.rss.instagram.feed.InstagramAccount
import ski.rss.instagram.feed.InstagramContentService
import ski.rss.instagram.feed.InstagramJsonParser
import ski.rss.socialaccount.SocialAccountRepository
import ski.rss.socialaccount.SocialContentRepository
import kotlin.test.Test
import kotlin.test.assertEquals

@ExperimentalCoroutinesApi
class InstagramContentServiceTest {
    private val contentRepository: SocialContentRepository = mockk(relaxUnitFun = true)
    private val accountRepository: SocialAccountRepository = mockk(relaxUnitFun = true)

    private val service = InstagramContentService(
        jsonParser = InstagramJsonParser(),
        contentRepository = contentRepository,
        accountRepository = accountRepository,
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

    @Test
    fun testSubscribe() {
        service.subscribe(account)

        verify { accountRepository.save(account) }
    }
}
