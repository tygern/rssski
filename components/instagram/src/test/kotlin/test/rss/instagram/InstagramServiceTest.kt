package test.rss.instagram

import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Test
import ski.rss.instagram.InstagramClient
import ski.rss.instagram.InstagramJsonParser
import ski.rss.instagram.InstagramService
import ski.rss.instagram.Result
import java.net.URI
import kotlin.test.assertEquals

@ExperimentalCoroutinesApi
class InstagramServiceTest {
    private val instagramClient: InstagramClient = mockk()

    private val service = InstagramService(
        instagramClient = instagramClient,
        jsonParser = InstagramJsonParser(URI("http://instagram.example.com")),
    )

    @Test
    fun testFetchProfile() = runBlockingTest {
        coEvery { instagramClient.fetchProfile("finnsadventures") } returns javaClass.getResource("/finnsadventures.json").readText()

        val result = service.fetchProfile("finnsadventures")

        require(result is Result.Success)
        assertEquals("finnsadventures", result.value.name)
    }

    @Test
    fun testJsonParseError() = runBlockingTest {
        coEvery { instagramClient.fetchProfile("givemejunk") } returns "<junk>"

        val result = service.fetchProfile("givemejunk")

        require(result is Result.Failure)
        assertEquals("Failed to parse JSON from Instagram response.", result.reason)
    }
}
