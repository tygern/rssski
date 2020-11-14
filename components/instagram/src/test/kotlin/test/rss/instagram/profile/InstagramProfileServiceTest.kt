package test.rss.instagram.profile

import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import ski.rss.functionalsupport.Failure
import ski.rss.instagram.profile.InstagramJsonParser
import ski.rss.instagram.profile.InstagramProfileService
import ski.rss.instagram.response.InstagramResponseRepository
import ski.rss.functionalsupport.Success
import ski.rss.instagram.profile.InstagramProfileRepository
import kotlin.test.Test
import kotlin.test.assertEquals

@ExperimentalCoroutinesApi
class InstagramProfileServiceTest {
    private val responseRepository: InstagramResponseRepository = mockk(relaxUnitFun = true)
    private val profileRepository: InstagramProfileRepository = mockk(relaxUnitFun = true)

    private val service = InstagramProfileService(
        jsonParser = InstagramJsonParser(),
        responseRepository = responseRepository,
        profileRepository = profileRepository,
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
    fun testNotFound() {
        coEvery { responseRepository.fetch(testName) } returns null

        val result = service.fetch(testName)

        require(result is Failure)
        assertEquals("Feed not found for Instagram account finnsadventures.", result.reason)
    }

    @Test
    fun testJsonParseError() {
        coEvery { responseRepository.fetch(testName) } returns "<junk>"

        val result = service.fetch(testName)

        require(result is Failure)
        assertEquals("Failed to parse JSON from Instagram response.", result.reason)
    }

    @Test
    fun testSave() {
        service.save("birds")

        verify { profileRepository.save("birds") }
    }
}
