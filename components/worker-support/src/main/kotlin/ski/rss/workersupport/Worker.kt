package ski.rss.workersupport

import ski.rss.functionalsupport.Result

interface Worker<T> {
    val name: String
    val numberOfThreads: Int
    fun canExecute(task: T): Boolean
    suspend fun execute(task: T): Result<Unit>
}
