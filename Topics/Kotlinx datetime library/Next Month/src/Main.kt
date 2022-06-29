import kotlinx.datetime.*

fun nextMonth(date: String): String {
    val dateParsed = Instant.parse(date)
    val period: DateTimePeriod = DateTimePeriod(months = 1)

    return dateParsed.plus(period, TimeZone.UTC).toString()
}

fun main() {
    val date = readLine()!!
    println(nextMonth(date))
}