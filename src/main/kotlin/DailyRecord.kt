package org.example

class DailyRecord(

){
     private var isMakeup: Boolean = false
    private var totalTime: Int = 0
     private var period:String = ""

    constructor(period: String) : this() {
        this.period = period
        this.isMakeup = false
        this.totalTime = getTotalTime()
    }

    fun getTotalTime(): Int{
        this.totalTime = 0
        val period=this.period
        period.split(" ").filter { it.isNotBlank() }.map{
            pair->
            val parts = pair.split("-")
            this.totalTime+=parts[1].toInt()-parts[0].toInt()
        }
        return this.totalTime
    }
     fun getPeriod() = period
     fun getMakeup() = isMakeup
     fun setMakeup(makeup: Boolean){
         isMakeup = makeup
     }
     fun setPeriod(period: String) {
         this.period = period
         this.totalTime = getTotalTime()
     }
}