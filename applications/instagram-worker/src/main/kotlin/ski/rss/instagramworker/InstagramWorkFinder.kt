package ski.rss.instagramworker

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import ski.rss.workersupport.WorkFinder

class InstagramWorkFinder : WorkFinder<String> {
    companion object {
        private val logger: Logger = LoggerFactory.getLogger(InstagramWorkFinder::class.java)
    }

    override fun findRequested(): List<String> {
        logger.info("Finding work")
        return listOf("accidentallywesanderson", "chelseafc")
    }
}
