package test.rss.twitter.feed

import io.mockk.Called
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import ski.rss.functionalsupport.Failure
import ski.rss.functionalsupport.Success
import ski.rss.socialaccount.SocialContentRepository
import ski.rss.twitter.feed.TwitterAccount
import ski.rss.twitter.feed.TwitterClient
import ski.rss.twitter.feed.TwitterContentStorageService
import kotlin.test.Test
import kotlin.test.assertEquals

@ExperimentalCoroutinesApi
class TwitterContentStorageServiceTest {
    private val twitterClient: TwitterClient = mockk()
    private val contentRepository: SocialContentRepository = mockk(relaxUnitFun = true)

    private val service = TwitterContentStorageService(
        twitterClient = twitterClient,
        contentRepository = contentRepository
    )

    private val account = TwitterAccount("finnsadventures")

    @Test
    fun testStoreContent() = runBlockingTest {
        coEvery { twitterClient.fetchContent(account) } returns Success("{\"some\": \"response\"}")

        val result = service.save(account)

        require(result is Success)
        verify { contentRepository.save(account, "{\"some\": \"response\"}") }
    }

    @Test
    fun testStoreContentFailure() = runBlockingTest {
        coEvery { twitterClient.fetchContent(account) } returns Failure("An error message")

        val result = service.save(account)

        require(result is Failure)
        assertEquals("An error message", result.reason)
        verify { contentRepository wasNot Called }
    }
}
