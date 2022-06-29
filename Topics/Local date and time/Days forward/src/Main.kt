import kotlinx.datetime.DatePeriod
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDate

fun daysForward(date: String, days: Int) {
    val localDate = date.toLocalDate()
    println(localDate.plus(DatePeriod(days = days)))
}

fun main() {
    val date = readLine()!!
    val days = readLine()!!.toInt()
    daysForward(date, days)
}