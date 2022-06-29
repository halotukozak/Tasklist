package tasklist

import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.datetime.*
import kotlinx.datetime.TimeZone
import java.io.File
import java.util.*
import kotlin.system.exitProcess


fun main() {
    TaskList.run()
}

object TaskList {

    private var tasks: MutableSet<Task> = mutableSetOf()
    private val type = Types.newParameterizedType(MutableSet::class.java, Task::class.java)
    private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
    private val adapter = moshi.adapter<MutableSet<Task>>(type)

    val emptyLinePattern = """^\s+$""".toRegex()
    val scanner = Scanner(System.`in`)
    private val validFields = listOf("priority", "date", "time", "task")
    private val validActions = listOf("add", "print", "edit", "delete", "end")

    private fun addTask() {
        val priority = Input.priority()
        val date = Input.date()
        val time = Input.time()
        val content = Input.content()

        val task = Task(content, time, date, priority)
        if (task.isEmpty()) {
            println("The task is blank")
            return
        }
        tasks.add(task)
    }

    fun run() {
        tasks = importData()
        while (true) {
            when (getAction()) {
                "add" -> addTask()
                "print" -> printTask()
                "delete" -> deleteTask()
                "edit" -> editTask()
                "end" -> exit()
                else -> println("The input action is invalid")
            }
        }
    }

    private fun importData(): MutableSet<Task> {
        val jsonFile = File("tasklist.json")
        if (jsonFile.exists()) {
            val dataJson = jsonFile.readText().trimIndent()
            val tasks = adapter.fromJson(dataJson)
            if (tasks != null && tasks.isNotEmpty()) {
                return tasks
            }
        }
        return mutableSetOf()
    }


    private fun deleteTask() {
        if (printTask()) {
            val task: Task = askForTaskNumber()
            tasks.remove(task)
            println("The task is deleted")
        }
    }

    private fun editTask() {
        if (printTask()) {
            val task: Task = askForTaskNumber()
            println("Input a field to edit (priority, date, time, task):")
            var field: String
            while (true) {
                println("Input a field to edit (priority, date, time, task):")
                field = readln().lowercase()
                if (field in validFields) break
                println("Invalid field")
            }
            val newTask = when (field) {
                "priority" -> task.copy(priority = Input.priority())
                "date" -> task.copy(time = task.time, date = Input.date())
                "time" -> task.copy(time = Input.time(), date = task.date)
                "task" -> task.copy(content = Input.content())
                else -> task
            }
            tasks.remove(task)
            tasks.add(newTask)
            println("The task is changed")
        }
    }


    private fun askForTaskNumber(): Task {
        while (true) {
            println("Input the task number (1-${tasks.size}):")
            val input = readln()
            val task: Task?
            if (Regex("\\d+").matches(input)) {
                task = tasks.elementAtOrNull(input.toInt() - 1)
                if (task != null) return task
            }
            println("Invalid task number")
        }
    }

    private fun exit() {
        exportData()
        println("Tasklist exiting!")
        exitProcess(0)
    }

    private fun exportData() {
        val dataJson = adapter.toJson(tasks)
        val jsonFile = File("tasklist.json")
        jsonFile.writeText(dataJson)
    }

    private fun printTask(): Boolean {
        return if (tasks.isEmpty()) {
            println("No tasks have been input")
            false
        } else {
            println(
                "+----+------------+-------+---+---+--------------------------------------------+\n" + "| N  |    Date    | Time  | P | D |                   Task                     |\n" + "+----+------------+-------+---+---+--------------------------------------------+"
            )
            tasks.forEachIndexed { index, task -> task.print(index + 1) }
            true
        }
    }


    private fun getAction(): String {
        while (true) {
            println("Input an action (add, print, edit, delete, end):")
            val action = readln()

            if (action in validActions) return action

            println("The input action is invalid")
        }
    }

}


@JsonClass(generateAdapter = true)
data class Task(
    private val content: MutableList<String> = mutableListOf(),
    val time: String,
    val date: String,
    val priority: String,
) {

    fun print(index: Int) {
        val currentDate = Clock.System.now().toLocalDateTime(TimeZone.of("UTC+0")).date
        val numberOfDays = currentDate.daysUntil(date.toLocalDate())
        val priority = when (priority) {
            "C" -> "\u001B[101m \u001B[0m"
            "H" -> "\u001B[103m \u001B[0m"
            "N" -> "\u001B[102m \u001B[0m"
            "L" -> "\u001B[104m \u001B[0m"
            else -> " "
        }

        val dueTag = when {
            numberOfDays == 0 -> "\u001B[103m \u001B[0m"
            numberOfDays > 0 -> "\u001B[102m \u001B[0m"
            else -> "\u001B[101m \u001B[0m"
        }

        val indent = if (index.toString().length == 1) " " else ""

        for ((i, line) in content.withIndex()) {
            val chunked = line.chunked(44)
            for ((j, chunkedLine) in chunked.withIndex()) {
                var finalLine = chunkedLine
                while (finalLine.length < 44) finalLine += " "
                if (i == 0 && j == 0) {
                    println("| $index$indent | $date | $time | $priority | $dueTag |$finalLine|")
                } else {
                    println("|    |            |       |   |   |$finalLine|")
                }
            }
        }
        println("+----+------------+-------+---+---+--------------------------------------------+")

    }

    fun isEmpty(): Boolean {
        return content.isEmpty()
    }
}

object Input {
    private val dateRegex = Regex(
        "^((2000|2400|2800|(19|2\\d)(0?[48]|[2468][048]|[13579][26]))-0?2-29)$" + "|^(((19|2\\d)\\d{2})-0?2-(0?[1-9]|1\\d|2[0-8]))$" + "|^(((19|2\\d)\\d{2})-(0?[13578]|10|12)-(0?[1-9]|[12]\\d|3[01]))$" + "|^(((19|2\\d)\\d{2})-(0?[469]|11)-(0?[1-9]|[12]\\d|30))$"
    )
    private val timeRegex = Regex("^([0-1]?\\d|2[0-3]):[0-5]\\d?\$")
    private val validPriority = listOf("C", "H", "N", "L")

    fun date(): String {
        while (true) {
            println("Input the date (yyyy-mm-dd):")
            var date = readln()
            if (dateRegex.matches(date)) {
                if ("^\\d{4}-\\d-\\d{1,2}$".toRegex().matches(date)) {
                    val temp = date.split("-")
                    date = temp[0] + "-0" + temp[1] + "-" + temp[2]
                }
                if ("^\\d{4}-\\d{1,2}-\\d$".toRegex().matches(date)) {
                    val temp = date.split("-")
                    date = temp[0] + "-" + temp[1] + "-0" + temp[2]
                }
                return date
            }
            println("The input date is invalid")
        }
    }

    fun time(): String {
        while (true) {
            var time: String
            println("Input the time (hh:mm):")
            time = readln()
            if (timeRegex.matches(time)) {
                if ("^\\d:\\d{1,2}$".toRegex().matches(time)) {
                    time = "0$time"
                }
                if ("^\\d{1,2}:\\d$".toRegex().matches(time)) {
                    time += "0"
                }
                return time
            }
            println("The input time is invalid")
        }
    }

    fun priority(): String {
        while (true) {
            println("Input the task priority (C, H, N, L):")
            val priority = readln().uppercase()
            if (priority in validPriority) return priority
        }

    }

    fun content(): MutableList<String> {
        println("Input a new task (enter a blank line to end):")
        val tasks = mutableListOf<String>()
        while (true) {
            val line = TaskList.scanner.nextLine().trimEnd().trimStart()
            if (line.isEmpty() || TaskList.emptyLinePattern.matches(line)) return tasks
            tasks.add(line)
        }
    }
}
