package org.example
import UserData
import java.time.LocalDate
import java.time.temporal.ChronoUnit

class AssistanceSystem {
    var id: Int = 0
    var supWeek:Int=0
    private val baseDate = LocalDate.of(2025, 4, 14)
    var nowWeek: Int = calculateWeeksFromBase()
    val users: MutableMap<Int,User> = mutableMapOf()
    private val fileStorage=UserData()

    init {
        nowWeek = calculateWeeksFromBase()
        // 从文件加载现有用户数据
        users.putAll(fileStorage.loadAllUsers())
        // 更新ID计数器到最大值+1
        id = users.keys.maxOrNull()?.plus(1) ?: 0
    }

    fun addUsers(newUsers: MutableList<User>){
        for (user in newUsers){
            this.users.put(this.id++,user)}
    fileStorage.saveAllUsers(this.users)
    }

    fun addUser(newUser: User){
        this.users.put(this.id++, newUser)
        fileStorage.saveAllUsers(this.users)
    }

    fun removeUser(id: Int){
        this.users.remove(id)
        fileStorage.saveAllUsers(this.users)
    }
    fun clockinCard(id:Int,day:Int,timePeriods: String){
        val user = users[id] ?: return
        val record = DailyRecord(timePeriods)
        user.weeklyRecords.getOrPut(nowWeek) { WeeklyRecord(day) }
            .dailyRecords[day] = record
        fileStorage.saveAllUsers(users)

    }
    fun supRecord(id: Int, day: Int, timePeriods: String, supWeek: Int) {
        val user = users[id] ?: return
        val record = DailyRecord(timePeriods).apply { setMakeup(true) }
        user.weeklyRecords.getOrPut(supWeek) { WeeklyRecord(day) }
            .dailyRecords[day] = record
        fileStorage.saveAllUsers(users)
    }
    fun getDailyTime(id: Int,day:Int):Int{
       val user = users[id] ?: return 0
        val weeklyRecords = user.weeklyRecords
       val weeklyRecord = weeklyRecords[nowWeek] ?: return 0
       val dailyRecords = weeklyRecord.dailyRecords
       val dailyRecord = dailyRecords[day]?: return 0
        return  dailyRecord.getTotalTime()
    }
    fun getWeeklyTime(id:Int,week:Int):Int{
        val user = users[id] ?: return 0
        var weeklyTime=0
        val weeklyRecords = user.weeklyRecords
        val weeklyRecord = weeklyRecords[week] ?: return 0
        for (dailyRecord in weeklyRecord.dailyRecords){
            weeklyTime+=dailyRecord.value.getTotalTime()
        }
        return  weeklyTime
    }
    private fun calculateWeeksFromBase(): Int {
        val currentDate = LocalDate.now()
        return ChronoUnit.WEEKS.between(baseDate, currentDate).toInt()+1
    }
}

