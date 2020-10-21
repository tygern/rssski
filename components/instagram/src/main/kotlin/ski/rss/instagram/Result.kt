package ski.rss.instagram

sealed class Result<T> {
    data class Success<T>(val value: T) : Result<T>()
    data class Failure<T>(val reason: String) : Result<T>()
}
