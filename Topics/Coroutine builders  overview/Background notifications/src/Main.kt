import kotlinx.coroutines.*
import kotlin.system.measureTimeMillis


fun doAllTheJob() {
    GlobalScope.launch {
        printProgress()
    }
    runBlocking {
        loadData()
    }
}
