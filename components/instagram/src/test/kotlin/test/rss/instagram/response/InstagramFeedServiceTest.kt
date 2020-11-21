package test.rss.instagram.response

import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import ski.rss.functionalsupport.Failure
import ski.rss.functionalsupport.Success
import ski.rss.instagram.response.InstagramFeedService
import ski.rss.instagram.response.InstagramJsonParser
import ski.rss.instagram.response.InstagramResponseRepository
import kotlin.test.Test
import kotlin.test.assertEquals

@ExperimentalCoroutinesApi
class InstagramFeedServiceTest {
    private val responseRepository: InstagramResponseRepository = mockk(relaxUnitFun = true)

    private val service = InstagramFeedService(
        jsonParser = InstagramJsonParser(),
        responseRepository = responseRepository
    )

    private val testName = "finnsadventures"

    @Test
    fun testFetchProfile() {
        coEvery { responseRepository.fetch(testName) } returns javaClass.getResource("/finnsadventures.json").readText()

        val result = service.fetch(testName)

        require(result is Success)
        assertEquals(testName, result.value.name)
    }

    @Test
    fun testFetchNotFound() {
        coEvery { responseRepository.fetch(testName) } returns null

        val result = service.fetch(testName)

        require(result is Failure)
        assertEquals("Feed not found for Instagram account finnsadventures.", result.reason)
    }

    @Test
    fun testFetchJsonParseError() {
        coEvery { responseRepository.fetch(testName) } returns "<junk>"

        val result = service.fetch(testName)

        require(result is Failure)
        assertEquals("Failed to parse JSON from Instagram response.", result.reason)
    }
}
