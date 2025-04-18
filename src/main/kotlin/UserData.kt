import org.example.DailyRecord
import org.example.User
import org.example.WeeklyRecord
import java.io.File

class UserData(private val filename: String = "users.txt") {
    // 保存所有用户数据
    fun saveAllUsers(users: MutableMap<Int, User>) {
        val content = users.entries.joinToString("\n") {
            "${it.key}|${it.value.serialize()}"
        }
        File(filename).writeText(content)
    }

    // 加载所有用户数据
    fun loadAllUsers(): MutableMap<Int, User> {
        val users = mutableMapOf<Int, User>()
        if (!File(filename).exists()) return users

        var currentId = -1
        var currentUser: User? = null
        var currentWeek = -1

        File(filename).forEachLine { line ->
            when {
                line.contains("|") -> {
                    val parts = line.split("|")
                    currentId = parts[0].toInt()
                    currentUser = User(parts[1], mutableMapOf())
                    users[currentId] = currentUser!!
                }
                line.startsWith("Week") -> {
                    currentWeek = line.removePrefix("Week").removeSuffix(":").toInt()
                }
                line.startsWith("星期") -> {
                    val (day, period, makeup) = parseDailyRecordLine(line)
                    val dailyRecord = DailyRecord(period).apply {
                        setMakeup(makeup)
                    }

                    currentUser?.weeklyRecords?.getOrPut(currentWeek) {
                        WeeklyRecord(day, mutableMapOf())
                    }?.dailyRecords?.put(day, dailyRecord)
                }
            }
        }
        return users
    }

    private fun parseDailyRecordLine(line: String): Triple<Int, String, Boolean> {
        val day = line.substringAfter("星期").substringBefore(":").toInt()
        val period = line.substringAfter("工作时间").substringBefore(",")
        val makeup = line.substringAfter("是否补卡").toBoolean()
        return Triple(day, period, makeup)
    }
}