package ski.rss.workersupport

interface WorkFinder<T> {
    fun findRequested(): List<T>
}
