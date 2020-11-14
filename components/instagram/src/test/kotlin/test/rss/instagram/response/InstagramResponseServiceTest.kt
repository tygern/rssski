package test.rss.instagram.response

import io.mockk.Called
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import ski.rss.functionalsupport.Failure
import ski.rss.instagram.response.InstagramClient
import ski.rss.instagram.response.InstagramResponseService
import ski.rss.instagram.response.InstagramResponseRepository
import ski.rss.functionalsupport.Success
import kotlin.test.Test
import kotlin.test.assertEquals

@ExperimentalCoroutinesApi
class InstagramResponseServiceTest {
    private val instagramClient: InstagramClient = mockk()
    private val responseRepository: InstagramResponseRepository = mockk(relaxUnitFun = true)

    private val service = InstagramResponseService(
        instagramClient = instagramClient,
        responseRepository = responseRepository
    )

    private val testName = "finnsadventures"

    @Test
    fun testStoreProfile() = runBlockingTest {
        coEvery { instagramClient.fetchProfile(testName) } returns Success("{\"some\": \"response\"}")

        val result = service.save(testName)

        require(result is Success)
        verify { responseRepository.save(testName, "{\"some\": \"response\"}") }
    }

    @Test
    fun testStoreProfileFailure() = runBlockingTest {
        coEvery { instagramClient.fetchProfile(testName) } returns Failure("An error message")

        val result = service.save(testName)

        require(result is Failure)
        assertEquals("An error message", result.reason)
        verify { responseRepository wasNot Called }
    }
}
