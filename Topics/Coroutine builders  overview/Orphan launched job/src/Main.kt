fun doAllTheJob() {
    val importantJob = GlobalScope.launch {
        connectToServer()
    }
}
