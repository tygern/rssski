package ski.rss.workersupport

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration

class WorkScheduler<T>(
    private val finder: WorkFinder<T>,
    private val workers: List<Worker<T>>,
    private val interval: Duration
) {
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
                worker.execute(work)
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
