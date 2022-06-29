suspend fun sendData() {
    delay(1)
    println("Success!")
}

// fix me, I want to call suspending function
fun main() {
    // Call suspending function from the article with following signature:
    // suspend fun sendData()
    sendData()
}