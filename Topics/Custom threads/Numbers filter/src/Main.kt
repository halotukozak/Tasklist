fun main() {
    val numbersFilter = thread(block = {
        var number = readln().toInt()
        while (number != 0) {
            if (number % 2 == 0) println(number)
            number = readln().toInt()
        }
    }, name =  "numbersFilter")
}