package ski.rss.workersupport

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.newFixedThreadPoolContext
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import ski.rss.functionalsupport.Failure
import ski.rss.functionalsupport.Success
import kotlin.time.Duration

@ObsoleteCoroutinesApi
class WorkScheduler<T>(
    private val finder: WorkFinder<T>,
    private val workers: List<Worker<T>>,
    private val interval: Duration,
) {
    companion object {
        private val logger: Logger = LoggerFactory.getLogger(WorkScheduler::class.java)
    }

    private val channel = Channel<T>(Channel.UNLIMITED)

    suspend fun start() = coroutineScope {
        everyInterval {
            findWork()
        }

        workers.forEach {
            listenForWork(it)
        }
    }

    private suspend fun findWork() {
        logger.info("Finding work")

        finder.findRequested().forEach { work ->
            logger.info("Found work: $work")
            channel.send(work)
        }
    }

    private fun CoroutineScope.listenForWork(worker: Worker<T>) =
        launch(newFixedThreadPoolContext(worker.numberOfThreads, worker.name)) {
            for (work in channel) {
                if (worker.canExecute(work)) {
                    logger.info("Worker ${worker.name} is starting to work on $work")

                    try {
                        when (worker.execute(work)) {
                            is Success -> logger.info("Worker ${worker.name} successfully completed $work")
                            is Failure -> logger.info("Worker ${worker.name} was unable to complete $work")
                        }
                    } catch (e: Throwable) {
                        logger.info("Worker ${worker.name} threw an exception for $work: $e")
                    }
                } else {
                    channel.send(work)
                }
            }
        }

    private fun CoroutineScope.everyInterval(block: suspend () -> Unit) =
        launch {
            while (true) {
                block()
                delay(interval.toLongMilliseconds())
            }
        }
}
