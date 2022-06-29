class MessageNotifier(val message: String, val repetitions: Int) : Thread() { // implement the constructor
    override fun run() {
        repeat(repetitions) {
            println(message)
        }
    }
}
