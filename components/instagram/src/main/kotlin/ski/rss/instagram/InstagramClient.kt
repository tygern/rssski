package ski.rss.instagram

import okhttp3.OkHttpClient
import okhttp3.Request
import java.net.URI

class InstagramClient(
    private val instagramUrl: URI,
    private val httpClient: OkHttpClient,
) {
    fun fetchProfile(name: String): String? {
        val url = "${instagramUrl.toString().trimEnd('/')}/$name?__a=1"
        val request = Request.Builder()
            .url(url)
            .build()

        httpClient.newCall(request).execute().use { response ->
            if (!response.isSuccessful) return null

            return response.body?.string()
        }
    }
}
