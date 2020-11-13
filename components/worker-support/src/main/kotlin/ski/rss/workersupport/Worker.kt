package ski.rss.workersupport

import ski.rss.functionalsupport.Result

interface Worker<T> {
    val name: String
    suspend fun execute(task: T): Result<Unit>
}
