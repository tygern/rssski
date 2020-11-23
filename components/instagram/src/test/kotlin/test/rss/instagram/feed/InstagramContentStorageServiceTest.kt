package test.rss.instagram.feed

import io.mockk.Called
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import ski.rss.functionalsupport.Failure
import ski.rss.functionalsupport.Success
import ski.rss.instagram.feed.InstagramAccount
import ski.rss.instagram.feed.InstagramClient
import ski.rss.instagram.feed.InstagramContentStorageService
import ski.rss.socialaccount.SocialContentRepository
import kotlin.test.Test
import kotlin.test.assertEquals

@ExperimentalCoroutinesApi
class InstagramContentStorageServiceTest {
    private val instagramClient: InstagramClient = mockk()
    private val contentRepository: SocialContentRepository = mockk(relaxUnitFun = true)

    private val service = InstagramContentStorageService(
        instagramClient = instagramClient,
        contentRepository = contentRepository,
    )

    private val account = InstagramAccount("finnsadventures")

    @Test
    fun testStoreContent() = runBlockingTest {
        coEvery { instagramClient.fetchProfile(account) } returns Success("{\"some\": \"response\"}")

        val result = service.storeContent(account)

        require(result is Success)
        verify { contentRepository.save(account, "{\"some\": \"response\"}") }
    }

    @Test
    fun testStoreContentFailure() = runBlockingTest {
        coEvery { instagramClient.fetchProfile(account) } returns Failure("An error message")

        val result = service.storeContent(account)

        require(result is Failure)
        assertEquals("An error message", result.reason)
        verify { contentRepository wasNot Called }
    }
}
