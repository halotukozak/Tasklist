fun doAllTheJob() {
    runBlocking{
        connectToServer()
        loadData()
    }
}
