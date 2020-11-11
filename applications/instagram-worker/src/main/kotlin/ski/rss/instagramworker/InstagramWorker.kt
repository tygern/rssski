package ski.rss.instagramworker

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import ski.rss.instagram.InstagramResponseCache
import ski.rss.workersupport.Worker

class InstagramWorker(
    override val name: String,
    private val instagramResponseCache: InstagramResponseCache
) : Worker<String> {
    companion object {
        private val logger: Logger = LoggerFactory.getLogger(InstagramWorker::class.java)
    }

    override suspend fun execute(task: String) {
        logger.info("Worker $name working on $task")

        instagramResponseCache.store(task)
    }
}
