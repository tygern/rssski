package ski.rss.workersupport

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import ski.rss.functionalsupport.Failure
import ski.rss.functionalsupport.Success
import kotlin.time.Duration

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

    private suspend fun findWork() =
        finder.findRequested().forEach { work ->
            channel.send(work)
        }

    private fun CoroutineScope.listenForWork(worker: Worker<T>) =
        launch {
            for (work in channel) {
                logger.info("Worker ${worker.name} is starting to work on $work")

                try {
                    when (worker.execute(work)) {
                        is Success -> logger.info("Worker ${worker.name} successfully completed $work")
                        is Failure -> logger.info("Worker ${worker.name} was unable to complete $work")
                    }
                } catch (e: Throwable) {
                    logger.info("Worker ${worker.name} threw an exception for $work: $e")
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
