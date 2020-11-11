package test.rss.instagram

import io.mockk.Called
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import ski.rss.instagram.InstagramClient
import ski.rss.instagram.InstagramResponseCache
import ski.rss.instagram.InstagramResponseRepository
import ski.rss.instagram.Result
import kotlin.test.Test
import kotlin.test.assertEquals

@ExperimentalCoroutinesApi
class InstagramResponseCacheTest {
    private val instagramClient: InstagramClient = mockk()
    private val responseRepository: InstagramResponseRepository = mockk(relaxUnitFun = true)

    private val cache = InstagramResponseCache(
        instagramClient = instagramClient,
        responseRepository = responseRepository
    )

    private val testName = "finnsadventures"

    @Test
    fun testStoreProfile() = runBlockingTest {
        coEvery { instagramClient.fetchProfile(testName) } returns Result.Success("{\"some\": \"response\"}")

        val result = cache.store(testName)

        require(result is Result.Success)
        verify { responseRepository.save(testName, "{\"some\": \"response\"}") }
    }

    @Test
    fun testStoreProfileFailure() = runBlockingTest {
        coEvery { instagramClient.fetchProfile(testName) } returns Result.Failure("An error message")

        val result = cache.store(testName)

        require(result is Result.Failure)
        assertEquals("An error message", result.reason)
        verify { responseRepository wasNot Called }
    }
}
