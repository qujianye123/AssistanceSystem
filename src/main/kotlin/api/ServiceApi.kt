package api
import java.net.ServerSocket
import org.example.AssistanceSystem
import org.example.User
fun startServer(port: Int) {
    val system = AssistanceSystem()

    ServerSocket(port).use { serverSocket ->
        println("服务端已启动，等待连接... (端口 $port)")

        while (true) {
            serverSocket.accept().use { clientSocket ->
                println("客户端IP: ${clientSocket.inetAddress.hostAddress}")

                clientSocket.getInputStream().bufferedReader().use { reader ->
                    clientSocket.getOutputStream().bufferedWriter().use { writer ->
                        try {
                            val request = reader.readLine()
                            println("收到原始请求: $request")

                            val response = processRequest(system, request)
                            println("返回响应: $response")

                            writer.write("$response\n")
                            writer.flush()
                        } catch (e: Exception) {
                            println("处理错误: ${e.javaClass.simpleName} - ${e.message}")
                        }
                    }
                }
                println("=== 连接处理完成 ===\n")
            }
        }
    }
}


private fun processRequest(system: AssistanceSystem, request: String): String {
    return try {
        val parts = request.split("|")
        when (parts[0]) {
            "ADD_USER" -> {
                val name = parts[1]
                val user = User(name, mutableMapOf())
                system.addUser(user)
                "用户添加成功"
            }

            "CLOCK_IN" -> {
                val params = parts[1].split(",")
                val userId = params[0].toInt()
                val day = params[1].toInt()
                val timePeriods = params[2]
                system.clockinCard(userId, day, timePeriods)
                "${system.users[userId]!!.name}打卡成功"
            }

            "GET_DAILY" -> {
                val params = parts[1].split(",")
                val hours = system.getTodayTime(params[0].toInt(), params[1].toInt())
                "本日工作时长：${hours}小时"
            }

            "GET_WEEKLY" -> {
                val params = parts[1].split(",")
                val hours = system.getWeeklyTime(params[0].toInt(), params[1].toInt())
                "本周工作时长：${hours}小时"
            }

            "SUP_CARD" ->{
                val params =parts[1].split(",")
                val id=params[0].toInt()
                val supWeek=params[1].toInt()
                val day =params[2].toInt()
                val hours=system.getSomedayTime(id, day,supWeek)
                system.supRecord(id,day,params[3],supWeek)
                "${system.users[id]!!.name}补卡成功 当日工作时长：${hours}小时"
            }
            else -> "ERROR|无效指令"
        }
    } catch (e: Exception) {
        "ERROR|请求处理失败: ${e.message}"
    }
}


