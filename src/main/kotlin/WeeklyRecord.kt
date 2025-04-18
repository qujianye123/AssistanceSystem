package org.example

class WeeklyRecord(
    val day: Int,
    val dailyRecords: MutableMap<Int,DailyRecord> = mutableMapOf(),
){
    constructor(day: Int, dailyRecord: DailyRecord) : this (
        day = day,
        dailyRecords = mutableMapOf(day to dailyRecord)
    )

    //ai生成序列化
    fun serializeDailyRecords(): String {
        return dailyRecords.entries
            .sortedBy { it.key } // 按日期（Int）升序排序
            .joinToString("\n") { (day, record) ->
                "星期$day:工作时长${record.getTotalTime()}小时,工作时间${record.getPeriod()},是否补卡${record.getMakeup()}"
            }
    }
}