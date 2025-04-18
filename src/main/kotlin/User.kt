package org.example

class User{
    var name: String
    var weeklyRecords: MutableMap<Int,WeeklyRecord>
    constructor(name:String,weeklyRecords: MutableMap<Int,WeeklyRecord>){
        this.name=name
        this.weeklyRecords = weeklyRecords
    }
    fun serialize(): String {
        val weeklyData = weeklyRecords.entries
            .sortedBy { it.key }
            .joinToString("\n") { (week, record) ->
                "Week$week:\n${record.serializeDailyRecords()}"
            }
        return "$name\n$weeklyData"
    }
}
