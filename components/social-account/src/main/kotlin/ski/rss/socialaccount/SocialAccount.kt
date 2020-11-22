package ski.rss.socialaccount

abstract class SocialAccount(
    open val username: String,
    private val platform: String,
) {
    override fun toString() = "$platform:$username"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SocialAccount

        if (username != other.username) return false
        if (platform != other.platform) return false

        return true
    }

    override fun hashCode(): Int {
        var result = username.hashCode()
        result = 31 * result + platform.hashCode()
        return result
    }

}
