package ski.rss.workersupport

interface Worker<T> {
    val name: String
    suspend fun execute(task: T)
}
