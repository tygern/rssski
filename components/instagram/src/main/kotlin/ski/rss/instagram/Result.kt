package ski.rss.instagram

sealed class Result<A> {
    data class Success<A>(val value: A) : Result<A>()
    data class Failure<A>(val reason: String) : Result<A>()

    fun <B> map(mapping: (A) -> B): Result<B> =
        when (this) {
            is Success -> Success(mapping(value))
            is Failure -> Failure(reason)
        }
}
