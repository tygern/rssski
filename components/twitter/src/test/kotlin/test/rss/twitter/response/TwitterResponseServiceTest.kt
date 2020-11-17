package test.rss.twitter.response

import io.mockk.Called
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import ski.rss.functionalsupport.Failure
import ski.rss.functionalsupport.Success
import ski.rss.twitter.response.TwitterClient
import ski.rss.twitter.response.TwitterResponseRepository
import ski.rss.twitter.response.TwitterResponseService
import kotlin.test.Test
import kotlin.test.assertEquals

@ExperimentalCoroutinesApi
class TwitterResponseServiceTest {
    private val twitterClient: TwitterClient = mockk()
    private val responseRepository: TwitterResponseRepository = mockk(relaxUnitFun = true)

    private val service = TwitterResponseService(
        twitterClient = twitterClient,
        responseRepository = responseRepository
    )

    private val testName = "finnsadventures"

    @Test
    fun testStoreProfile() = runBlockingTest {
        coEvery { twitterClient.fetchProfile(testName) } returns Success("{\"some\": \"response\"}")

        val result = service.save(testName)

        require(result is Success)
        verify { responseRepository.save(testName, "{\"some\": \"response\"}") }
    }

    @Test
    fun testStoreProfileFailure() = runBlockingTest {
        coEvery { twitterClient.fetchProfile(testName) } returns Failure("An error message")

        val result = service.save(testName)

        require(result is Failure)
        assertEquals("An error message", result.reason)
        verify { responseRepository wasNot Called }
    }
}
