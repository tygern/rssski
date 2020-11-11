package test.rss.instagram

import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import ski.rss.instagram.InstagramClient
import ski.rss.instagram.InstagramResponseCache
import ski.rss.instagram.InstagramResponseRepository
import kotlin.test.Test

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
        coEvery { instagramClient.fetchProfile(testName) } returns "{\"some\": \"response\"}"

        cache.store(testName)

        verify { responseRepository.save(testName, "{\"some\": \"response\"}") }
    }
}
