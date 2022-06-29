fun main() {
    val workerA = Thread(RunnableWorker(), "worker-A")
    val workerB = Thread(RunnableWorker(), "worker-B")
    val workerC = Thread(RunnableWorker(), "worker-C")

    workerA.start()
    workerB.start()
    workerC.start()
}

// Don't change the code below       
class RunnableWorker : Runnable {
    override fun run() {
        val name = Thread.currentThread().name
        if (name.startsWith("worker-")) {
            println("too hard calculations...")
        } else {
            return
        }
    }
}